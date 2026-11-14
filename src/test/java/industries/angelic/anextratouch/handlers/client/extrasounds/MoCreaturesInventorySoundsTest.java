package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MoCreaturesInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            MoCreaturesInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.MoCreatures." + name;
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
        // Reptile and scorpion chitin gear use custom materials; stings even claim gold.
        aliases("category.gear.armadillo", "reptileplate", "scorphelmetnether", "scorpstingcave");
        aliases("category.gear.leather", "furchest", "hideboots", "whip", "horsesaddle");
        aliases("category.gear.wood", "bo", "staffportal");
        aliases("category.amulet", "petamulet", "amuletfairyfull", "medallion");
        aliases("pickup.minecraft.bone", "sharksword", "unicornhorn", "bigcatclaw");
        assertFalse(events.has("item.pickup.MoCreatures.katana"));
    }

    @Test
    public void materialsFoodAndBlocksFollowTheirFamilies() {
        // Per jack, chitin, scales and teeth share one light wooden click.
        aliases("category.piece", "chitin", "chitinnether", "sharkteeth");
        aliases("category.potion", "essencefire", "bowlwater", "bowlfish3");
        aliases("category.bottle_empty", "bowlempty");
        aliases("category.meat", "heartundead", "turkeycooked", "crabraw");
        aliases("category.paper", "scrollofsale");
        aliases("category.leaves", "MoCLeaves", "MoCTallGrass");
        assertFalse(events.has("item.pickup.MoCreatures.achievement_icon_tame_kitty"));
    }
}
