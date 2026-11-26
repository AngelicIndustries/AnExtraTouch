package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BattlegearInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            BattlegearInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.battlegear2." + name;
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
    public void shieldsAndGearFollowTheirMaterial() {
        // Shields are plain items the classifier hears as iron shields.
        aliases("category.gear.wood", "shield.wood");
        aliases("category.gear.leather", "shield.hide", "quiver");
        aliases("category.gear.golden", "shield.gold");
        aliases("category.gear.diamond", "shield.diamond");
        aliases("category.gear.iron", "knights_armour.plate", "knights_armour.boots");
        aliases("category.gear.chain", "chain");
        aliases("category.arrow", "mb.arrow");
        // Weapons are swords with vanilla tool materials and keep the classifier's cue.
        assertFalse(events.has("item.pickup.battlegear2.mace.iron"));
        assertFalse(events.has("item.pickup.battlegear2.shield.iron"));
    }
}
