package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ExtraSoundDefinitionsTest {

    private static final String ROOT = "/assets/anextratouch/";

    @Test
    public void bundledDefinitionsHaveFilesAndResolvableAcyclicLocalAliases() throws Exception {
        JsonObject events;
        try (InputStream stream = getClass().getResourceAsStream(ROOT + "sounds.json")) {
            assertNotNull(stream);
            events = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject();
        }
        for (Map.Entry<String, JsonElement> event : events.entrySet()) {
            check(events, event.getKey(), new HashSet<String>());
        }
    }

    @Test
    public void backpackCuesRetainTheAuditionedEventsPitchAndVolume() throws Exception {
        JsonObject events;
        try (InputStream stream = getClass().getResourceAsStream(ROOT + "sounds.json")) {
            events = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject();
        }
        alias(events, "backpack.leather.equip", "minecraft:item.armor.equip_leather", 1, 1);
        alias(events, "backpack.leather.unequip", "minecraft:item.armor.equip_leather", 1, 0.9);
        alias(events, "backpack.iron.equip", "minecraft:item.armor.equip_iron", 1, 1);
        alias(events, "backpack.iron.unequip", "minecraft:item.armor.equip_iron", 1, 0.9);
        alias(events, "backpack.hose.mode", "anextratouch:generic.scroll", 1, 1);
        alias(events, "backpack.hose.tank", "anextratouch:generic.scroll", 1, 0.83);
        alias(events, "backpack.cycling.on", "minecraft:block.wooden_button.click_on", 1, 1);
        alias(events, "backpack.cycling.off", "minecraft:block.wooden_button.click_off", 1, 1);
        alias(events, "backpack.tank.fill", "minecraft:item.bucket.empty", 0.65, 1);
        alias(events, "backpack.tank.drain", "minecraft:item.bucket.fill", 0.65, 1);
        alias(events, "backpack.tank.empty", "minecraft:item.bucket.empty", 0.65, 0.9);
    }

    private static void alias(JsonObject events, String event, String target, double volume, double pitch) {
        JsonObject definition = events.getAsJsonObject(event);
        assertEquals(
            "player",
            definition.get("category")
                .getAsString());
        JsonObject entry = definition.getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        assertEquals(
            target,
            entry.get("name")
                .getAsString());
        assertEquals(
            volume,
            entry.get("volume")
                .getAsDouble(),
            0.00001);
        assertEquals(
            pitch,
            entry.get("pitch")
                .getAsDouble(),
            0.00001);
    }

    @Test
    public void otherNamespacesNameFilesByPathAndEventsByType() throws Exception {
        JsonObject events;
        try (InputStream stream = getClass().getResourceAsStream(ROOT + "sounds.json")) {
            events = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .getAsJsonObject();
        }
        for (Map.Entry<String, JsonElement> event : events.entrySet()) {
            for (JsonElement value : event.getValue()
                .getAsJsonObject()
                .getAsJsonArray("sounds")) {
                JsonObject entry = value.isJsonObject() ? value.getAsJsonObject() : null;
                String name = entry == null ? value.getAsString()
                    : entry.get("name")
                        .getAsString();
                if (name.startsWith("anextratouch:")) continue;
                boolean isEvent = entry != null && entry.has("type")
                    && "event".equals(
                        entry.get("type")
                            .getAsString());
                // A file outside AET is a path (minecraft:random/pop); a dotted name is an event that needs its type,
                // or the game looks for a file named after it and plays nothing.
                if (!isEvent) assertTrue(
                    event.getKey() + " names " + name + " as a file",
                    name.substring(name.indexOf(':') + 1)
                        .contains("/"));
            }
        }
    }

    @Test
    public void everyModernDefaultHasAFallbackAndLocalFallbacksExist() throws Exception {
        JsonObject events, fallbacks;
        try (InputStream definitions = getClass().getResourceAsStream(ROOT + "sounds.json");
            InputStream defaults = getClass().getResourceAsStream(ROOT + "extra_sound_fallbacks.json")) {
            events = new JsonParser().parse(new InputStreamReader(definitions, StandardCharsets.UTF_8))
                .getAsJsonObject();
            fallbacks = new JsonParser().parse(new InputStreamReader(defaults, StandardCharsets.UTF_8))
                .getAsJsonObject();
        }
        for (Map.Entry<String, JsonElement> event : events.entrySet()) {
            for (JsonElement value : event.getValue()
                .getAsJsonObject()
                .getAsJsonArray("sounds")) {
                if (!value.isJsonObject()) continue;
                JsonObject entry = value.getAsJsonObject();
                String name = entry.get("name")
                    .getAsString();
                if (entry.has("type") && "event".equals(
                    entry.get("type")
                        .getAsString())
                    && name.startsWith("minecraft:")) assertTrue("Missing fallback for " + name, fallbacks.has(name));
            }
        }
        for (Map.Entry<String, JsonElement> fallback : fallbacks.entrySet()) {
            String name = fallback.getValue()
                .getAsString();
            if (name.startsWith("anextratouch:"))
                assertTrue("Missing fallback event " + name, events.has(name.substring("anextratouch:".length())));
        }
    }

    private void check(JsonObject events, String event, Set<String> visiting) throws Exception {
        assertTrue("Missing event " + event, events.has(event));
        assertTrue("Circular alias at " + event, visiting.add(event));
        for (JsonElement value : events.getAsJsonObject(event)
            .getAsJsonArray("sounds")) {
            JsonObject entry = value.isJsonObject() ? value.getAsJsonObject() : null;
            String name = entry == null ? value.getAsString()
                : entry.get("name")
                    .getAsString();
            if (name.contains(":")) {
                if (!name.startsWith("anextratouch:")) continue;
                name = name.substring("anextratouch:".length());
            }
            if (entry != null && entry.has("type")
                && "event".equals(
                    entry.get("type")
                        .getAsString())) {
                check(events, name, visiting);
            } else {
                try (InputStream file = getClass().getResourceAsStream(ROOT + "sounds/" + name + ".ogg")) {
                    assertNotNull("Missing file for " + event + ": " + name, file);
                }
            }
        }
        visiting.remove(event);
    }
}
