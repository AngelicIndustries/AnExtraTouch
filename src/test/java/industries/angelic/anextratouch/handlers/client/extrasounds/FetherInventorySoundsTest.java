package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FetherInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            FetherInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.fether." + name;
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
    public void quartzGearSoundsLikeDiamondAndNetherFoodFollowsItsFamilies() {
        // Fether's QUARTZ materials are unknown to the classifier, which gave generic gear; per jack, like diamond.
        aliases("category.gear.diamond", "quartz_chestplate", "quartz_pickaxe", "quartz_sword");
        aliases("category.metal", "quartz_ingot");
        aliases("category.fruit", "ignis_fruit", "marrow_berry");
        aliases("category.leaves", "blood_leaf", "nether_leaves", "nether_sapling");
        // Per jack, weevils and their soft eggs sound like meat.
        aliases("category.meat", "flesh_root", "cooked_weevil", "weevil", "weevil_egg");
        aliases("category.dust", "blood_leaf_seeds", "glow_flower_seeds");
        aliases("category.banner", "nether_bed");
        assertFalse(events.has("item.pickup.fether.nether_planks"));
    }
}
