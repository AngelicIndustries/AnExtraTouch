package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpiceOfLifeInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            SpiceOfLifeInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private String key(String name) {
        return "item.pickup.SpiceOfLife." + name;
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
    public void registeredItemsUsePaperPaperBagAndMetalRatherThanGenericContact() {
        // The journal is a plain Item, so the book classifier cannot reach it.
        aliases("bookfoodjournal", "category.paper");
        // A crinkly bag, not a turning page.
        aliases("lunchbag", "category.paper_bag");
        // Crafted from heavy weighted pressure plates, so iron rather than tin or wood.
        aliases("lunchbox", "category.bucket");
    }

    @Test
    public void paperBagIsABundledRecording() {
        JsonObject sound = events.getAsJsonObject("item.category.paper_bag")
            .getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        assertEquals(
            "anextratouch:item/paper_bag/contact",
            sound.get("name")
                .getAsString());
        assertNotNull(
            SpiceOfLifeInventorySoundsTest.class
                .getResourceAsStream("/assets/anextratouch/sounds/item/paper_bag/contact.ogg"));
    }

    @Test
    public void containerContentsAndLidStateDoNotSelectDefinitions() {
        // Contents and the open flag are NBT; metadata is always zero.
        for (String container : new String[] { "lunchbag", "lunchbox" }) {
            assertFalse(events.has(key(container + ".accent")));
            assertFalse(events.has(key(container + ".0")));
            assertFalse(events.has(key(container + "_open_full")));
        }
        // The mod ID is case-sensitive in registry names.
        assertFalse(events.has("item.pickup.spiceoflife.lunchbox"));
    }
}
