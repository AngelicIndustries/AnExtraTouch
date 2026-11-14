package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WildMobsInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            WildMobsInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.wildmobsmod." + name;
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
    public void plainItemsLeaveTheGenericPickup() {
        // Every Wild Mobs item is a plain item, so even its buckets fell back to the generic cue.
        aliases("category.gear.leather", "bison_leather_chestplate", "fur_boots");
        aliases("category.meat", "venison", "cooked_goose", "infected_flesh");
        aliases("category.leaves", "butterfly", "dragonfly");
        aliases("category.piece", "armadillo_shell");
        aliases("pickup.minecraft.slime_ball", "slime_drop", "magma_cream_drop");
        aliases("category.bucket", "jellyfish_bucket", "nether_jellyfish_bucket", "sea_scorpion_bucket");
        aliases("category.egg", "sea_scorpion_egg");
        aliases("pickup.minecraft.leather", "cougar_hide", "fur");
        aliases("pickup.minecraft.spawn_egg", "spawn_egg_hyena");
    }

    @Test
    public void creatureBucketsSloshLikeWaterBuckets() {
        // The liquid accent plays alongside the bucket cue, as for vanilla's water and lava buckets.
        aliases(
            "category.bucket_liquid",
            "jellyfish_bucket.accent",
            "nether_jellyfish_bucket.accent",
            "sea_scorpion_bucket.accent");
    }

    @Test
    public void theMiredBottleWhispersLikeBopsPixieJar() {
        // A dry bottle with the creature's voice on top, as BOP's pixie jar.
        aliases("category.bottle_empty", "mired_bottle");
        JsonObject sound = events.getAsJsonObject("item.pickup.wildmobsmod.mired_bottle.accent")
            .getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        // One of the mired's own whispers, chosen at random by Wild Mobs' event.
        assertEquals(
            "wildmobsmod:entity.mired.say",
            sound.get("name")
                .getAsString());
        assertEquals(
            "event",
            sound.get("type")
                .getAsString());
        // A little quieter and higher than the mired itself.
        assertTrue(
            sound.get("volume")
                .getAsFloat() < 1);
        assertTrue(
            sound.get("pitch")
                .getAsFloat() > 1);
    }
}
