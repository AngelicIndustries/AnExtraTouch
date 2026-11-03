package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CookingInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            CookingInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private String key(String name) {
        return "item.pickup.cookingforblockheads." + name;
    }

    private void aliases(String name, String target) {
        JsonObject event = events.getAsJsonObject(key(name));
        assertNotNull("Missing registered item: " + name, event);
        assertEquals(
            "player",
            event.get("category")
                .getAsString());
        assertEquals(
            1,
            event.getAsJsonArray("sounds")
                .size());
        JsonObject sound = event.getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        assertEquals(
            "event",
            sound.get("type")
                .getAsString());
        assertEquals(
            "anextratouch:item." + target,
            sound.get("name")
                .getAsString());
    }

    @Test
    public void registeredItemsUseFoodPaperAndLightMetalRatherThanGenericContact() {
        aliases("recipebook", "category.paper");
        // All three registered book tiers share the base paper event.
        for (int metadata : new int[] { 3, 0, 1 }) {
            assertFalse(events.has(key("recipebook." + metadata)));
        }
        aliases("toast", "pickup.minecraft.bread");
        aliases("toaster", "category.bucket");
    }

    @Test
    public void ovenAndFridgeKeepTheirMetalBlockSound() {
        // The light metal contact sounded like a bucket on these large appliances.
        for (String appliance : new String[] { "cookingoven", "fridge" }) {
            assertFalse(events.has(key(appliance)));
        }
    }

    @Test
    public void emptySinkAndBundledToolTexturesDoNotImplyLiquidOrItemDefinitions() {
        assertFalse(events.has(key("sink.accent")));
        for (String texture : new String[] { "pot", "saucepan", "skillet", "bakeware", "recipebook_tier0" }) {
            assertFalse(events.has(key(texture)));
        }
    }
}
