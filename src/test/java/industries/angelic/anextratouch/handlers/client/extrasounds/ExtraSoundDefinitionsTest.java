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
