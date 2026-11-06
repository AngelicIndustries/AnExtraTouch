package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import industries.angelic.anextratouch.Config;

/** Local accents. Volume and pitch on the event's entries come from Extra Sounds' definitions. */
public final class ExtraSounds {

    private ExtraSounds() {}

    public static ResourceLocation id(String event) {
        return new ResourceLocation("anextratouch", event);
    }

    public static void ui(String event) {
        ui(id(event), 1.0F);
    }

    public static void ui(ResourceLocation event, float pitch) {
        if (Config.extraSoundsVolume <= 0) return;
        SoundHandler handler = Minecraft.getMinecraft()
            .getSoundHandler();
        ui(event, pitch, Config.extraSoundsVolume, handler::getSound, handler::playSound);
    }

    static void ui(ResourceLocation event, float pitch, float volume,
        Function<ResourceLocation, SoundEventAccessorComposite> lookup, Consumer<ISound> play) {
        if (volume <= 0) return;
        play.accept(new UiSound(event, volume, pitch));
        // sounds.json entries are random alternatives. A companion event plays simultaneously.
        if (!"anextratouch".equals(event.getResourceDomain()) || !event.getResourcePath()
            .startsWith("item.pickup.")) return;
        ResourceLocation accent = id(event.getResourcePath() + ".accent");
        SoundEventAccessorComposite definition = lookup.apply(accent);
        // Missing optional mods and empty pack replacements must stay silent.
        if (definition != null && definition.func_148721_a() > 0) play.accept(new UiSound(accent, volume, pitch));
    }

    public static void at(String event, double x, double y, double z, float volume, float pitch) {
        if (Config.extraSoundsVolume <= 0) return;
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(
                new PositionedSoundRecord(
                    id(event),
                    volume * Config.extraSoundsVolume,
                    pitch,
                    (float) x,
                    (float) y,
                    (float) z));
    }

    public static void drop(ItemStack stack, int count) {
        if (!Config.extraItemDrops || stack == null || count <= 0) return;
        ui(id("item.drop"), varied(dropPitch(count, stack.isStackable() ? stack.getMaxStackSize() : 1)));
    }

    /** A single or unstackable item at 1.2, down to 0.9 for a full stack. */
    public static float dropPitch(int count, int maximum) {
        return maximum <= 1 ? 1.2F : 1.2F - 0.3F * Math.max(0, Math.min(1, (count - 1.0F) / (maximum - 1.0F)));
    }

    /** Each drop varies by up to 6% so repeated drops don't sound identical. */
    static float varied(float pitch) {
        return pitch * (0.94F + (float) Math.random() * 0.12F);
    }

    public static void poof(Entity entity) {
        if (!Config.extraEntityPoof || !entity.worldObj.isRemote) return;
        float size = (float) Math.sqrt(entity.height * entity.width) * 0.4F;
        float pitch = size + (2 - size) * 0.65F + (float) ((Math.random() - 0.5) / 3);
        at("entity.poof", entity.posX, entity.posY, entity.posZ, 0.7F, pitch);
    }

    static final class UiSound extends PositionedSoundRecord {

        UiSound(ResourceLocation event, float volume, float pitch) {
            super(event, volume, pitch, 0, 0, 0);
            this.field_147666_i = ISound.AttenuationType.NONE;
        }
    }
}
