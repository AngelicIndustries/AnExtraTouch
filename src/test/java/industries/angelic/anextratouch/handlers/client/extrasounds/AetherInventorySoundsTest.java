package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AetherInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            AetherInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.aether_legacy." + name;
            assertTrue("Missing case-sensitive registration: " + key, events.has(key));
            JsonObject definition = events.getAsJsonObject(key);
            assertEquals(
                "player",
                definition.get("category")
                    .getAsString());
            JsonObject sound = definition.getAsJsonArray("sounds")
                .get(0)
                .getAsJsonObject();
            assertEquals(
                name,
                "anextratouch:item." + target,
                sound.get("name")
                    .getAsString());
            assertEquals(
                "event",
                sound.get("type")
                    .getAsString());
        }
    }

    @Test
    public void gearSoundsLikeItsMaterialNotItsTier() {
        // Skyroot tools are stone tier, tipped holystone ones iron, Neptune armor diamond.
        aliases("category.gear.wood", "skyroot_pickaxe", "amplified_skyroot_sword", "cloud_staff");
        aliases("category.gear.stone", "holystone_axe", "tipped_holystone_shovel");
        aliases("category.gear.chain", "neptune_chestplate", "scaled_neptune_boots", "neptune_gloves");
        aliases("category.gear.netherite", "obsidian_helmet", "amplified_obsidian_gloves", "heavy_blaze_gloves");
        aliases("category.gear.golden", "valkyrie_lance", "golden_gloves", "phoenix_leggings");
        aliases("category.gear.iron", "arkenium_sword", "divineral_hoe", "iron_gloves");
        aliases("category.gear.diamond", "zanite_pickaxe", "gravitite_shield", "ascensite_sword");
        aliases("category.gear.leather", "leather_gloves", "agility_boots");
        aliases("category.ring", "iron_ring", "amplified_valkyrie_ring", "haste_ring");
        aliases("category.amulet", "zanite_pendant", "golden_pendant", "victory_medal");
        aliases("pickup.minecraft.bone", "bone_ring");
        aliases("pickup.minecraft.amethyst_shard", "ice_ring", "ice_pendant");
        // Per jack, auralite pendants chain like a pendant with the amethyst ding on top.
        aliases("category.amulet", "auralite_pendant", "amplified_auralite_pendant");
        aliases("pickup.minecraft.amethyst_shard", "auralite_pendant.accent", "amplified_auralite_pendant.accent");
        aliases("category.banner", "red_cape", "valkyrie_cape", "invisibility_cape");
        assertFalse(events.has("item.pickup.aether_legacy.notch_hammer"));
    }

    @Test
    public void materialsAndFoodFollowTheirVanillaFamilies() {
        aliases("category.metal", "zanite_gemstone", "arkenium_ingot");
        aliases("category.loose_metal", "raw_gravitite");
        aliases("pickup.minecraft.amethyst_shard", "ambrosium_shard", "icestone_crystal", "power_shard");
        aliases("pickup.minecraft.slime_ball", "swet_ball", "gummy_swet");
        aliases("pickup.minecraft.snowball", "blue_aercloud_globule", "cold_parachute");
        aliases("category.keys", "dungeon_key");
        aliases("category.fruit", "blueberry", "enchanted_orange", "white_apple");
        aliases("category.meat", "raw_aerwhale");
        aliases("category.dust", "strawberry_seeds", "cockatrice_feather");
    }

    @Test
    public void crystalBlocksChimeAndFoliageRustles() {
        aliases("pickup.minecraft.amethyst_shard", "auralite_cluster_1", "aceninum_cluster");
        aliases("pickup.minecraft.amethyst_block", "block_of_auralite");
        aliases("category.leaves", "skyroot_leaves", "golden_oak_sapling");
        // Aerclouds, quicksoil, holystone and skyroot already sound like their material.
        assertFalse(events.has("item.pickup.aether_legacy.aercloud"));
        assertFalse(events.has("item.pickup.aether_legacy.skyroot_planks"));
    }
}
