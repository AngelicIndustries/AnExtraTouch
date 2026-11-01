package industries.angelic.anextratouch.compat;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.Loader;
import industries.angelic.anextratouch.compat.etfuturum.EfrBlockSounds;
import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;

/** Translate backported identities to the existing modern inventory definitions. */
public final class EfrInventoryCompat {

    private static Boolean available;

    private EfrInventoryCompat() {}

    public static ResourceLocation itemSound(String registryName, int metadata, Predicate<ResourceLocation> exists) {
        if (registryName == null || !registryName.startsWith("etfuturum:")) return null;
        String name = registryName.substring("etfuturum:".length());
        String modern = modernName(name, metadata);
        ResourceLocation variant = ExtraSounds.id("item.pickup.minecraft." + modern + "." + metadata);
        if (exists.test(variant)) return variant;
        ResourceLocation event = ExtraSounds.id("item.pickup.minecraft." + modern);
        if (exists.test(event)) return event;
        String category = category(name, modern);
        ResourceLocation family = category == null ? null : ExtraSounds.id("item.category." + category);
        return family != null && exists.test(family) ? family : null;
    }

    static String modernName(String name, int metadata) {
        switch (name) {
            case "mutton_raw":
                return "mutton";
            case "mutton_cooked":
                return "cooked_mutton";
            case "rabbit_raw":
                return "rabbit";
            case "rabbit_cooked":
                return "cooked_rabbit";
            case "wooden_armorstand":
                return "armor_stand";
            case "nugget_iron":
                return "iron_nugget";
            case "chorus_fruit_popped":
                return "popped_chorus_fruit";
            case "glow_berries_item":
                return "glow_berries";
            case "netherite_spade":
                return "netherite_shovel";
            case "raw_ore":
                String[] ores = { "raw_copper", "raw_iron", "raw_gold" };
                return metadata >= 0 && metadata < ores.length ? ores[metadata] : name;
            case "dye":
                String[] dyes = { "white_dye", "blue_dye", "brown_dye", "black_dye" };
                return metadata >= 0 && metadata < dyes.length ? dyes[metadata] : name;
            default:
                return name;
        }
    }

    private static String category(String name, String modern) {
        // These families are generated from item types upstream rather than listed per item.
        if (name.endsWith("_boat") || name.endsWith("_raft")) return "boat";
        if (name.endsWith("_record")) return "music_disc";
        if (name.equals("dye")) return "dust";
        if (name.equals("raw_ore") || name.equals("modded_raw_ore") || modern.startsWith("raw_")) return "loose_metal";
        if (name.equals("elytra")) return "gear.leather";
        if (name.equals("tipped_arrow") || name.equals("spectral_arrow")) return "arrow";
        if (name.equals("banner") || name.endsWith("_bed")) return "banner";
        if (name.startsWith("item_sign_") || name.equals("coopers_mallet")) return "wood";
        if (name.equals("disc_fragment_5")) return "metal_bits";
        if (name.equals("shulker_box_upgrade") || name.equals("barrel_upgrade")) return "metal_sheets";
        if (modern.startsWith("netherite_") && (modern.endsWith("_helmet") || modern.endsWith("_chestplate")
            || modern.endsWith("_leggings")
            || modern.endsWith("_boots")
            || modern.endsWith("_sword")
            || modern.endsWith("_pickaxe")
            || modern.endsWith("_axe")
            || modern.endsWith("_shovel")
            || modern.endsWith("_hoe"))) return "gear.netherite";
        return null;
    }

    public static Block.SoundType blockSound(Block block, int metadata) {
        if (available == null) available = Loader.isModLoaded("etfuturum");
        return available ? EfrBlockSounds.sound(block, metadata) : block.stepSound;
    }
}
