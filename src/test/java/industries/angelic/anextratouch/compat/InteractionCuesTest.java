package industries.angelic.anextratouch.compat;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import net.minecraft.init.Blocks;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import industries.angelic.anextratouch.network.message.MessageInteractionCue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class InteractionCuesTest {

    @Test
    public void containersFillOrEmptyByTheirKind() {
        assertEquals(InteractionCues.Cue.BUCKET_FILL, InteractionCues.container(true, true));
        assertEquals(InteractionCues.Cue.BUCKET_EMPTY, InteractionCues.container(true, false));
        assertEquals(InteractionCues.Cue.BOTTLE_FILL, InteractionCues.container(false, true));
        assertEquals(InteractionCues.Cue.BOTTLE_EMPTY, InteractionCues.container(false, false));
    }

    @Test
    public void harvestingNeedsAChangedPlant() {
        assertFalse(InteractionCues.changed(Blocks.wheat, 7, Blocks.wheat, 7));
        assertTrue(InteractionCues.changed(Blocks.wheat, 7, Blocks.wheat, 0));
        assertTrue(InteractionCues.changed(Blocks.wheat, 7, Blocks.air, 0));
    }

    @Test
    public void harvestCraftCropsAreCutTornOrPulled() {
        assertEquals(InteractionCues.Cue.HARVEST_GRAIN, InteractionCues.crop("harvestcraft:pambarleyCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_GRAIN, InteractionCues.crop("harvestcraft:pamriceCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_LEAFY, InteractionCues.crop("harvestcraft:pamlettuceCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_LEAFY, InteractionCues.crop("harvestcraft:pambrusselsproutCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_LEAFY, InteractionCues.crop("harvestcraft:leafygarden"));
        assertEquals(InteractionCues.Cue.HARVEST, InteractionCues.crop("harvestcraft:pamstrawberryCrop"));
        assertEquals(InteractionCues.Cue.HARVEST, InteractionCues.crop("harvestcraft:pamtomatoCrop"));
        assertEquals(InteractionCues.Cue.HARVEST, InteractionCues.crop("harvestcraft:pamchilipepperCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop("harvestcraft:pamcornCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop("harvestcraft:pamonionCrop"));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop("harvestcraft:berrygarden"));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop(null));
        // Hunger Overhaul also harvests vanilla and Natura crops.
        assertEquals(InteractionCues.Cue.HARVEST_GRAIN, InteractionCues.crop("minecraft:wheat", 7));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop("minecraft:carrots", 7));
        assertEquals(InteractionCues.Cue.HARVEST_GRAIN, InteractionCues.crop("Natura:N Crops", 3));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop("Natura:N Crops", 8));
        assertEquals(InteractionCues.Cue.HARVEST, InteractionCues.crop("harvestcraft:pamtomatoCrop", 7));
        // Netherlicious: abyssal oat is cut like grain, devilish maize tears like other crops.
        assertEquals(InteractionCues.Cue.HARVEST_GRAIN, InteractionCues.crop("netherlicious:AbyssalOat", 7));
        assertEquals(InteractionCues.Cue.HARVEST_CROP, InteractionCues.crop("netherlicious:DevilishMaize", 7));
    }

    @Test
    public void churningIsLowerAndVariesMore() throws Exception {
        JsonObject churn = events().getAsJsonObject("action.churn")
            .getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        assertTrue(
            churn.get("pitch")
                .getAsFloat() < 1);
        assertTrue(InteractionCues.Cue.CHURN.spread > InteractionCues.Cue.HARVEST.spread);
    }

    @Test
    public void bundledCueRecordingsExist() throws Exception {
        for (InteractionCues.Cue cue : InteractionCues.Cue.values()) {
            for (com.google.gson.JsonElement sound : events().getAsJsonObject(cue.event)
                .getAsJsonArray("sounds")) {
                String name = sound.getAsJsonObject()
                    .get("name")
                    .getAsString();
                if (!name.startsWith("anextratouch:action/")) continue;
                String file = "/assets/anextratouch/sounds/" + name.substring("anextratouch:".length()) + ".ogg";
                assertNotNull("Missing " + file, getClass().getResourceAsStream(file));
            }
        }
    }

    private JsonObject events() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            getClass().getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            return new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    @Test
    public void cuesSurviveThePacketAndUnknownIdsAreIgnored() {
        for (InteractionCues.Cue cue : InteractionCues.Cue.values()) {
            ByteBuf buf = Unpooled.buffer();
            new MessageInteractionCue(cue, -30000000, 255, 12).toBytes(buf);
            MessageInteractionCue read = new MessageInteractionCue();
            read.fromBytes(buf);
            assertSame(cue, InteractionCues.Cue.byId(read.cue));
            assertEquals(-30000000, read.x);
            assertEquals(255, read.y);
            assertEquals(12, read.z);
        }
        assertNull(InteractionCues.Cue.byId(-1));
        assertNull(InteractionCues.Cue.byId(InteractionCues.Cue.values().length));
    }

    @Test
    public void everyCueHasABlockSoundEvent() throws Exception {
        JsonObject events;
        try (InputStreamReader reader = new InputStreamReader(
            getClass().getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
        for (InteractionCues.Cue cue : InteractionCues.Cue.values()) {
            JsonObject event = events.getAsJsonObject(cue.event);
            assertNotNull("Missing event " + cue.event, event);
            // World interactions follow the Blocks volume slider, like vanilla's block sounds.
            assertEquals(
                "block",
                event.get("category")
                    .getAsString());
        }
    }
}
