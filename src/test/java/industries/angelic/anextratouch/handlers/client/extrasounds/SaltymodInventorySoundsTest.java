package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SaltymodInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            SaltymodInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private String target(String item, int metadata) {
        String key = "item.pickup.saltymod." + item;
        if (events.has(key + "." + metadata)) key += "." + metadata;
        assertTrue("Missing registry item: " + item, events.has(key));
        JsonObject definition = events.getAsJsonObject(key);
        assertEquals(
            "player",
            definition.get("category")
                .getAsString());
        assertEquals(
            1,
            definition.getAsJsonArray("sounds")
                .size());
        JsonObject sound = definition.getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        assertEquals(
            "event",
            sound.get("type")
                .getAsString());
        return sound.get("name")
            .getAsString();
    }

    private void aliases(String family, String... names) {
        for (String name : names) {
            assertEquals(name, "anextratouch:item." + family, target(name, 0));
        }
    }

    @Test
    public void saltedAndCookedVariantsKeepTheirMaterial() {
        for (int metadata = 0; metadata < 3; metadata++) {
            for (String meat : new String[] { "strider", "haunch", "tailor", "calamari" }) {
                assertEquals("anextratouch:item.category.meat", target(meat, metadata));
            }
        }
        for (int metadata = 0; metadata < 2; metadata++) {
            assertEquals("anextratouch:item.category.bowl", target("chicken_soup", metadata));
            assertEquals("anextratouch:item.pickup.minecraft.bowl", target("fruit_salad", metadata));
            assertEquals("anextratouch:item.category.fruit", target("golden_berries", metadata));
        }
        // These are variant translation names, not separate registrations.
        assertFalse(events.has("item.pickup.saltymod.salt_chicken_soup"));
        assertFalse(events.has("item.pickup.saltymod.cooked_tailor"));
    }

    @Test
    public void dryMealsSoupAndPreservesUseDifferentContainers() {
        aliases(
            "pickup.minecraft.bowl",
            "egg_bowl",
            "pumpkin_porridge",
            "potato_mushroom",
            "dandelion_salad",
            "saltwort_cooked_beef",
            "bop_salt_rice_bowl",
            "tf_saltwort_meef_steak");
        aliases("category.bowl", "salt_mushroom_stew", "fish_soup", "melon_soup", "tf_salt_meef_stroganoff");
        aliases(
            "category.potion",
            "fermented_mushroom",
            "pickled_onion",
            "apple_preserves",
            "royal_jelly",
            "fizzy_drink",
            "tunneler_concoction",
            "bop_pickled_turnip",
            "tf_pickled_mushgloom");
        assertFalse(events.has("item.pickup.saltymod.fizzy_drink.accent"));
    }

    @Test
    public void looseIngredientsAndFoodsReuseAcceptedFamilies() {
        aliases("category.dust", "salt", "salt_pinch", "sugar_pinch", "powdered_milk", "bop_salt_shroom_powder");
        aliases("category.fruit", "sugar_apple", "honey_apple", "chocolate_berries");
        aliases("category.crunchy_dry", "onion", "salt_beetroot", "sweetberry_cookie");
        aliases("pickup.minecraft.bread", "salt_bread", "dough", "muffin");
        aliases("category.meat", "salt_cooked_beef", "wm_salt_cooked_bison");
        aliases("pickup.minecraft.slime_ball", "tough_jelly", "bop_poison");
    }

    @Test
    public void gearAndSpecialBlocksUseMaterialsWithoutReplacingNativeBlockSounds() {
        // These are washable mineral-mud coatings, not hardened mud equipment.
        aliases("pickup.minecraft.mud", "mud_helmet", "mud_chestplate", "mud_leggings", "mud_boots");
        aliases("pickup.minecraft.mud", "mineral_mud_ball");
        aliases("pickup.minecraft.bone", "sheep_horn");
        aliases("pickup.minecraft.firework_star", "rainmaker_star");
        aliases("pickup.minecraft.firework_rocket", "rainmaker");
        aliases("category.bucket", "cooking_pot");
        for (String crystal : new String[] { "salt_shard", "salt_pickaxe", "salt_crystal" }) {
            assertEquals("minecraft:block.glass.place", target(crystal, 0));
        }
        // These already have appropriate cloth, wood or native mud step sounds.
        for (String block : new String[] { "storage_sack", "storage_crate", "storage_barrel", "mineral_mud",
            "wet_mud_brick", "dry_mud_brick" }) {
            assertFalse(events.has("item.pickup.saltymod." + block));
        }
        assertFalse(events.has("item.pickup.saltymod.cooking_pot.accent"));
        assertFalse(events.has("item.pickup.saltymod.dev_item"));
    }
}
