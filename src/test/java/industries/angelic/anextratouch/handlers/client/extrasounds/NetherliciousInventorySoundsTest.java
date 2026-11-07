package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NetherliciousInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            NetherliciousInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.netherlicious." + name;
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
    public void gearFollowsItsMaterial() {
        aliases("category.gear.iron", "EfrineChestPlate", "EfrinePickaxe", "EfrineHoe");
        aliases("category.gear.netherite", "HeavyBlazeHelmet", "HeavyBlazeBoots");
        aliases("category.bottle_empty", "GlassesWhite", "GlassesDealmaker");
        aliases("pickup.minecraft.flint_and_steel", "FlintAndSteelSoul", "FlintAndSteelShadow");
        aliases("category.wood", "foxfireSign", "door_foxfire");
    }

    @Test
    public void materialsAndFoodFollowTheirVanillaFamilies() {
        aliases("category.metal", "Ingot");
        aliases("category.metal_bits", "Nugget");
        aliases("pickup.minecraft.amethyst_shard", "CrystalShard", "VoidQuartzItem");
        aliases("pickup.minecraft.netherite_scrap", "Materials");
        aliases("pickup.minecraft.phantom_membrane", "Materials.1");
        // Netherlicious' seeds are plain items, so the classifier would not find them.
        aliases("category.dust", "GloomstoneDust", "WartItem", "AbyssalOatSeeds", "HellderBerrySeeds");
        aliases("category.fruit", "HellderBerryItem");
        aliases("category.meat", "StriderFlankRaw", "StriderFlankCooked");
        aliases("category.potion", "JuiceHellderberry");
        // Popcorn is a soup item, but its bowl holds nothing liquid.
        aliases("pickup.minecraft.bowl", "DevilishPopcorn", "DevilishPopcornRaw");
        assertFalse(events.has("item.pickup.netherlicious.StewCrimson"));
    }
}
