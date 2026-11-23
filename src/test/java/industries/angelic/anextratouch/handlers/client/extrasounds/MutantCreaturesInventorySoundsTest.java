package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MutantCreaturesInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            MutantCreaturesInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.MutantCreatures.item." + name;
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
    public void plainItemsAndIronBoneArmorFollowWhatTheyAreMadeOf() {
        // Every item but the armor was a plain item, and the bone armor reports iron.
        aliases("pickup.minecraft.bone", "skeletonPart", "skeleArmorHead", "skeleArmorChest", "skeleArmorBoots");
        aliases("category.gear.leather", "skeletonPart.3");
        aliases("category.potion", "chemicalX", "creeperStats");
        aliases("pickup.minecraft.amethyst_shard", "creeperShard");
        aliases("pickup.minecraft.ender_pearl", "endersoulHand");
        aliases("category.gear.stone", "hulkHammer");
        aliases("pickup.minecraft.spawn_egg", "mcSpawnEgg");
    }
}
