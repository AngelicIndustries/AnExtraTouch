package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import industries.angelic.anextratouch.compat.EfrInventoryCompat;
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
        SoundEventAccessorComposite variantSound = handler.getSound(variant);
        if (variantSound != null && variantSound != generated.get(variant)) return variant;
        ResourceLocation filled = filledWaterBag(name, stack.getTagCompound());
        if (filled != null && handler.getSound(filled) != null) return filled;
        SoundEventAccessorComposite existing = handler.getSound(event);
        if (existing != null && existing != generated.get(event)) return event;
        ResourceLocation translated = EfrInventoryCompat
            .itemSound(name, stack.getItemDamage(), id -> handler.getSound(id) != null);
        ResourceLocation fallback = translated == null ? fallback(stack) : translated;
        // Keep simultaneous sounds for different subtypes independent, and recheck generated
        // variants on every lookup so config/resource changes still take effect.
        if (stack.getHasSubtypes()) {
            event = variant;
            existing = variantSound;
        }
        if (existing == null || !fallback.equals(fallbacks.get(event))) {
            SoundEventAccessorComposite definition = new SoundEventAccessorComposite(
                event,
                1,
                1,
                SoundCategory.PLAYERS);
            boolean block = translated == null && stack.getItem() instanceof ItemBlock;
            float volume = block ? 0.6F : 1.0F;
            float pitch = block ? 1.7F : 1.0F;
            definition.addSoundToEventPool(new ExtraSoundResources.Alias(handler, fallback, volume, pitch, 1));
            ((AccessorExtraSoundHandler) handler).anextratouch$soundRegistry()
                .registerSound(definition);
            generated.put(event, definition);
            fallbacks.put(event, fallback);
        }
        return event;
    }

    static ResourceLocation filledWaterBag(String name, NBTTagCompound tag) {
        return filledWaterBag(name, tag, fluid -> FluidRegistry.getFluid(fluid) != null);
    }

    static ResourceLocation filledWaterBag(String name, NBTTagCompound tag, Predicate<String> registeredFluid) {
        if (!"Growthcraft|Cellar:grc.waterBag".equals(name) || tag == null || !tag.hasKey("Fluid", 10)) return null;
        // Growthcraft stores color in metadata and a Forge FluidStack in this tag.
        // Read every stack so filling or draining cannot leave a stale cached cue.
        NBTTagCompound fluid = tag.getCompoundTag("Fluid");
        return fluid.getInteger("Amount") > 0 && registeredFluid.test(fluid.getString("FluidName"))
            ? ExtraSounds.id("item.pickup.Growthcraft|Cellar.grc.waterBag.filled")
            : null;
    }

    static ResourceLocation fallback(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            int metadata = item.getMetadata(stack.getItemDamage());
            // Small BOP mushrooms should follow small vanilla mushrooms, including EFR's sound changes.
            if ("BiomesOPlenty:mushrooms".equals(Item.itemRegistry.getNameForObject(item))) {
                block = Blocks.brown_mushroom;
                metadata = 0;
            }
            return new ResourceLocation(
                EfrInventoryCompat.blockSound(block, metadata)
                    .getStepResourcePath());
        }
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
                return gear("generic");
            case SHIELD:
                return gear("IRON");
            case SWORD:
                return gear(item instanceof ItemSword ? ((ItemSword) item).getToolMaterialName() : "IRON");
            case AXE:
            case TOOL:
                return gear(
                    item instanceof ItemTool ? ((ItemTool) item).getToolMaterialName()
                        : item instanceof ItemHoe ? ((ItemHoe) item).getToolMaterialName() : "IRON");
            default:
                return utility(item);
        }
    }

    private static ResourceLocation utility(Item item) {
        // These families are generated at runtime upstream, not listed in its sounds.json.
        if (item instanceof ItemBoat) return category("boat");
        if (item instanceof ItemMinecart) return category("minecart");
        if (item instanceof ItemHangingEntity) return category("frame");
        if (item instanceof ItemDye || item instanceof ItemSeeds) return category("dust");
        if (item instanceof ItemRecord) return category("music_disc");
        if (item instanceof ItemSaddle || item instanceof ItemLead) return gear("leather");
        if (item instanceof ItemMap || item instanceof ItemEmptyMap || item instanceof ItemNameTag)
            return category("paper");
        if (item instanceof ItemFishingRod || item instanceof ItemCarrotOnAStick) return gear("generic");
        if (item instanceof ItemSoup) return category("bowl");
        if (item instanceof ItemEgg) return category("egg");
        if (item instanceof ItemBucket) return category("bucket");
        if (item instanceof ItemExpBottle) return category("potion");
        return ExtraSounds.id("item.pickup");
    }

    private static ResourceLocation category(String name) {
        return ExtraSounds.id("item.category." + name);
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
