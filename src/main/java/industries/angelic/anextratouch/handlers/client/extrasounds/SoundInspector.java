package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.mixins.early.minecraft.AccessorFallbackResources;
import industries.angelic.anextratouch.mixins.early.minecraft.AccessorReloadableResources;
import industries.angelic.anextratouch.mixins.early.minecraft.AccessorSoundEventComposite;

/**
 * Answers what a sound event really plays: through the live sound registry (after every resource pack and AET's
 * aliases and modern-sound fallbacks), down to each file and the resource pack that provides it. Tracing reports the
 * entry the sound manager actually picked each time a sound plays.
 */
public final class SoundInspector {

    private static final int MAX_DEPTH = 8, SAMPLES = 16;
    /** Null while tracing is off; empty traces every sound to the log only. */
    private static volatile String traceFilter;

    private SoundInspector() {}

    public static void trace(String filter) {
        traceFilter = filter;
    }

    public static String traceFilter() {
        return traceFilter;
    }

    /** The entry the sound manager chose for this sound, just before it plays. */
    public static void played(ISound sound, SoundPoolEntry entry) {
        String filter = traceFilter;
        if (filter == null || sound == null || entry == null) return;
        String event = String.valueOf(sound.getPositionedSoundLocation());
        ResourceLocation file = entry.getSoundPoolEntryLocation();
        if (!filter.isEmpty() && !event.contains(filter)
            && !String.valueOf(file)
                .contains(filter))
            return;
        // SoundManager clamps the pitch like this; volume is further scaled by the category slider.
        float pitch = MathHelper.clamp_float((float) (sound.getPitch() * entry.getPitch()), 0.5F, 2.0F);
        String line = event + " -> "
            + file
            + " ["
            + provider(file)
            + "] volume "
            + number(sound.getVolume() * entry.getVolume())
            + ", pitch "
            + number(pitch);
        AnExtraTouch.LOG.info("Sound trace: {}", line);
        if (!filter.isEmpty()) chat(EnumChatFormatting.GRAY + line);
    }

    public static List<String> describe(ResourceLocation event) {
        List<String> out = new ArrayList<>();
        SoundHandler handler = Minecraft.getMinecraft()
            .getSoundHandler();
        SoundEventAccessorComposite composite = handler.getSound(event);
        if (composite == null) {
            composite = ModernSounds.lookup(handler::getSound, event, ModernSounds.efrNamespace());
            if (composite == null) {
                out.add(event + " is not a sound event.");
                return out;
            }
            out.add(
                event + " is not registered itself; AET's aliases resolve it to " + composite.getSoundEventLocation());
        }
        describe(handler, composite, 1, 1, 1, 0, new HashSet<>(), out);
        return out;
    }

    private static void describe(SoundHandler handler, SoundEventAccessorComposite composite, double chance,
        double volume, double pitch, int depth, Set<ResourceLocation> path, List<String> out) {
        String indent = indent(depth);
        ResourceLocation id = composite.getSoundEventLocation();
        AccessorSoundEventComposite access = (AccessorSoundEventComposite) composite;
        volume *= access.anextratouch$eventVolume();
        pitch *= access.anextratouch$eventPitch();
        out.add(
            indent + EnumChatFormatting.YELLOW
                + id
                + EnumChatFormatting.RESET
                + " ("
                + composite.getSoundCategory()
                    .getCategoryName()
                + ", "
                + percent(chance)
                + ") defined by "
                + definedBy(id));
        if (depth >= MAX_DEPTH || !path.add(id)) {
            out.add(indent + "  stops here: loop or too deep");
            return;
        }
        int total = composite.func_148721_a();
        for (ISoundEventAccessor entry : access.anextratouch$soundPool()) {
            double share = total > 0 ? chance * entry.func_148721_a() / total : 0;
            if (entry instanceof SoundEventAccessorComposite) {
                describe(handler, (SoundEventAccessorComposite) entry, share, volume, pitch, depth + 1, path, out);
            } else if (entry instanceof ExtraSoundResources.Alias) {
                ExtraSoundResources.Alias alias = (ExtraSoundResources.Alias) entry;
                SoundEventAccessorComposite target = alias.resolve();
                out.add(
                    indent + "  AET alias -> "
                        + alias.target()
                        + " (volume x"
                        + number(alias.volume())
                        + ", pitch x"
                        + number(alias.pitch())
                        + ")");
                if (target == null) out.add(indent + "    " + EnumChatFormatting.RED + "missing, plays nothing");
                else describe(
                    handler,
                    target,
                    share,
                    volume * alias.volume(),
                    pitch * alias.pitch(),
                    depth + 2,
                    path,
                    out);
            } else {
                ResourceLocation reference = eventReference(entry);
                if (reference != null) {
                    SoundEventAccessorComposite target = handler.getSound(reference);
                    out.add(indent + "  event reference -> " + reference);
                    if (target == null) out.add(indent + "    " + EnumChatFormatting.RED + "missing, plays nothing");
                    else describe(handler, target, share, volume, pitch, depth + 2, path, out);
                } else {
                    files(entry, share, volume, pitch, indent, out);
                }
            }
        }
        path.remove(id);
    }

    /** A file entry: sampled, since other mods' entries may pick among several. */
    private static void files(ISoundEventAccessor entry, double share, double volume, double pitch, String indent,
        List<String> out) {
        Set<String> seen = new LinkedHashSet<>();
        for (int i = 0; i < SAMPLES; i++) {
            Object value = entry.func_148720_g();
            if (!(value instanceof SoundPoolEntry)) continue;
            SoundPoolEntry sample = (SoundPoolEntry) value;
            ResourceLocation file = sample.getSoundPoolEntryLocation();
            String line = indent + "  file "
                + EnumChatFormatting.AQUA
                + file
                + EnumChatFormatting.RESET
                + " from "
                + EnumChatFormatting.GREEN
                + provider(file)
                + EnumChatFormatting.RESET
                + ", volume "
                + number(volume * sample.getVolume())
                + ", pitch "
                + number(pitch * sample.getPitch());
            if (seen.add(line)) out.add(line + ", " + percent(share));
        }
        if (seen.size() > 1) out.add(
            indent + "  ("
                + entry.getClass()
                    .getName()
                + " picks among these)");
    }

    /** Vanilla's {@code "type": "event"} entries are anonymous classes holding the referenced event. */
    private static ResourceLocation eventReference(ISoundEventAccessor entry) {
        for (Field field : entry.getClass()
            .getDeclaredFields()) {
            if (!ResourceLocation.class.isAssignableFrom(field.getType())) continue;
            try {
                field.setAccessible(true);
                return (ResourceLocation) field.get(entry);
            } catch (ReflectiveOperationException | RuntimeException e) {
                return null;
            }
        }
        return null;
    }

    /** The resource pack (or mod jar) that supplies the file: the highest pack that has it. */
    public static String provider(ResourceLocation file) {
        List<IResourcePack> packs = packs(file.getResourceDomain());
        if (packs == null) return "no pack has the domain " + file.getResourceDomain();
        for (int i = packs.size() - 1; i >= 0; i--) {
            if (packs.get(i)
                .resourceExists(file))
                return packs.get(i)
                    .getPackName();
        }
        return EnumChatFormatting.RED + "MISSING" + EnumChatFormatting.RESET;
    }

    /** Every pack whose sounds.json defines the event, lowest first; later ones add to it or replace it. */
    private static String definedBy(ResourceLocation event) {
        List<IResourcePack> packs = packs(event.getResourceDomain());
        if (packs == null) return "code";
        ResourceLocation json = new ResourceLocation(event.getResourceDomain(), "sounds.json");
        List<String> names = new ArrayList<>();
        for (IResourcePack pack : packs) {
            if (!pack.resourceExists(json)) continue;
            try (InputStream in = pack.getInputStream(json)) {
                JsonElement root = new JsonParser().parse(new InputStreamReader(in, StandardCharsets.UTF_8));
                if (!root.isJsonObject() || !root.getAsJsonObject()
                    .has(event.getResourcePath())) continue;
                JsonElement definition = root.getAsJsonObject()
                    .get(event.getResourcePath());
                boolean replace = definition.isJsonObject() && definition.getAsJsonObject()
                    .has("replace")
                    && definition.getAsJsonObject()
                        .get("replace")
                        .getAsBoolean();
                names.add(pack.getPackName() + (replace ? " (replaces)" : ""));
            } catch (Exception unreadable) {
                names.add(pack.getPackName() + " (unreadable sounds.json)");
            }
        }
        return names.isEmpty() ? "code" : String.join(", ", names);
    }

    @SuppressWarnings("unchecked")
    private static List<IResourcePack> packs(String domain) {
        IResourceManager manager = Minecraft.getMinecraft()
            .getResourceManager();
        if (!(manager instanceof AccessorReloadableResources)) return null;
        Map<String, ?> domains = ((AccessorReloadableResources) manager).anextratouch$domainManagers();
        Object fallback = domains.get(domain);
        return fallback instanceof AccessorFallbackResources
            ? ((AccessorFallbackResources) fallback).anextratouch$resourcePacks()
            : null;
    }

    private static void chat(String line) {
        if (Minecraft.getMinecraft().thePlayer != null)
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(line));
    }

    private static String indent(int depth) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < depth; i++) out.append("  ");
        return out.toString();
    }

    private static String number(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static String percent(double chance) {
        return String.format(Locale.ROOT, "%.0f%%", chance * 100);
    }
}
