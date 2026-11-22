package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MowziesMobsInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            MowziesMobsInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.mowziesmobs." + name;
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
    public void plainItemsAndMisreportedGearFollowWhatTheyAreMadeOf() {
        // The masks report chain mail and the spear stone; both are carved wood.
        aliases("category.gear.wood", "barakoaMask1", "barakoaMask5", "spear");
        aliases("pickup.minecraft.bamboo", "blowgun");
        aliases("category.arrow", "dart");
        aliases("category.dust", "foliaathseed");
        aliases("pickup.minecraft.spawn_egg", "spawnEgg");
        // The wrought axe and helm report iron and keep the classifier's iron cue; painted acacia sounds like wood.
        assertFalse(events.has("item.pickup.mowziesmobs.wroughtaxe"));
        assertFalse(events.has("item.pickup.mowziesmobs.wroughthelm"));
        assertFalse(events.has("item.pickup.mowziesmobs.paintedAcacia"));
    }
}
