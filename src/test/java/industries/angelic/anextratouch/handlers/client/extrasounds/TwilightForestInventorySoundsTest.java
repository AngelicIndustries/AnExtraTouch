package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TwilightForestInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            TwilightForestInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.TwilightForest." + name;
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
    public void gearSoundsLikeItsMaterial() {
        // Every Twilight Forest set uses its own material, which the classifier gave generic gear.
        // Per jack, naga scales sound dry like armadillo scutes, not the soft turtle shell.
        aliases("category.gear.armadillo", "item.plateNaga", "item.legsNaga");
        aliases("category.gear.iron", "item.ironwoodPick", "item.knightlyPlate", "item.phantomHelm");
        aliases("category.gear.netherite", "item.fierySword", "item.fieryBoots");
        aliases("category.gear.leather", "item.steeleafAxe", "item.yetiPlate", "item.arcticBoots");
        aliases("category.gear.stone", "item.giantPick");
        aliases("category.amulet", "item.charmOfLife1", "item.charmOfKeeping3");
        aliases("category.ring", "item.knightmetalRing");
        assertFalse(events.has("item.pickup.TwilightForest.item.tripleBow"));
    }

    @Test
    public void materialsFoodAndBlocksFollowTheirFamilies() {
        aliases("category.metal", "item.knightMetal", "item.fieryIngot");
        aliases("category.dust", "item.tfFeather", "item.magicBeans");
        aliases("category.meat", "item.venisonRaw", "item.meefSteak", "item.critter", "tile.TFFirefly");
        aliases("category.paper", "item.magicMap", "item.emptyMazeMap");
        aliases("pickup.minecraft.bread", "item.experiment115");
        // Trapdoors set no sound and fell back to stone.
        aliases("category.wood", "item.doorCanopy", "tile.TFTrapDoorCanopy");
        aliases("category.leaves", "tile.TFLeaves", "tile.TFSapling");
        aliases("category.bottle_empty", "tile.TFFireflyJar");
    }
}
