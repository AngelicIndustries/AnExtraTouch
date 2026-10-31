package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;

import industries.angelic.anextratouch.handlers.client.ItemSoundRegistry;
import industries.angelic.anextratouch.mixins.early.minecraft.AccessorExtraSoundHandler;

/** Per-item pack events take precedence; unknown items use AET's classifier and blocks' step sounds. */
public final class InventorySoundRegistry {

    private static final Map<ResourceLocation, SoundEventAccessorComposite> generated = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> fallbacks = new HashMap<>();

    private InventorySoundRegistry() {}

    public static ResourceLocation sound(ItemStack stack) {
        SoundHandler handler = Minecraft.getMinecraft()
            .getSoundHandler();
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        ResourceLocation event = ExtraSounds.id("item.pickup." + (name == null ? "unknown" : name.replace(':', '.')));
        // Resource packs can supply per-metadata accents as well as the upstream per-item event.
        ResourceLocation variant = ExtraSounds.id(event.getResourcePath() + "." + stack.getItemDamage());
        if (handler.getSound(variant) != null) return variant;
        SoundEventAccessorComposite existing = handler.getSound(event);
        if (existing != null && existing != generated.get(event)) return event;
        ResourceLocation fallback = fallback(stack);
        if (existing == null || !fallback.equals(fallbacks.get(event))) {
            SoundEventAccessorComposite definition = new SoundEventAccessorComposite(
                event,
                1,
                1,
                SoundCategory.PLAYERS);
            float volume = stack.getItem() instanceof ItemBlock ? 0.6F : 1.0F;
            float pitch = stack.getItem() instanceof ItemBlock ? 1.7F : 1.0F;
            definition.addSoundToEventPool(new ExtraSoundResources.Alias(handler, fallback, volume, pitch, 1));
            ((AccessorExtraSoundHandler) handler).anextratouch$soundRegistry()
                .registerSound(definition);
            generated.put(event, definition);
            fallbacks.put(event, fallback);
        }
        return event;
    }

    private static ResourceLocation fallback(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock)
            return new ResourceLocation(Block.getBlockFromItem(item).stepSound.getStepResourcePath());
        if (item instanceof ItemArmor) return gear(
            ((ItemArmor) item).getArmorMaterial()
                .name());
        switch (ItemSoundRegistry.resolve(stack)) {
            case NONE:
                return ExtraSounds.id("muted");
            case POTION:
                return ExtraSounds.id("item.category.potion");
            case BOOK:
                return ExtraSounds.id("item.category.paper");
            case BOW:
            case CROSSBOW:
                return ExtraSounds.id("item.pickup.minecraft.bow");
            case SHIELD:
                return gear("IRON");
            case SWORD:
                return gear(item instanceof ItemSword ? ((ItemSword) item).getToolMaterialName() : "IRON");
            case AXE:
            case TOOL:
                return gear(item instanceof ItemTool ? ((ItemTool) item).getToolMaterialName() : "IRON");
            default:
                return ExtraSounds.id("item.pickup");
        }
    }

    private static ResourceLocation gear(String material) {
        String category = material.toLowerCase(Locale.ROOT);
        if ("cloth".equals(category)) category = "leather";
        if ("gold".equals(category)) category = "golden";
        if ("emerald".equals(category)) category = "diamond";
        switch (category) {
            case "wood":
            case "stone":
            case "iron":
            case "diamond":
            case "golden":
            case "leather":
            case "chain":
                break;
            default:
                category = "generic";
        }
        return ExtraSounds.id("item.category.gear." + category);
    }
}
