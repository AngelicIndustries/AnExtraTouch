package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PlantMegaPackInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            PlantMegaPackInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.plantmegapack." + name;
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
    public void produceFollowsHarvestCraftsFamilies() {
        aliases("category.fruit", "foodTomato", "berriesStraw", "berriesHarlequinMistletoe", "foodPricklyPearFruit");
        aliases("category.crunchy_dry", "foodCorn", "foodBellPepperRed", "foodLettuce", "foodTaroRoot");
        aliases("category.leaves", "foodSpinach", "foodWatercress", "leafWildMint", "flowerCattail");
        aliases("category.wet", "foodMozukuSeaweed");
        aliases("category.dust", "foodPeanuts", "foodRice", "foodCornFlour", "seedTomato", "powderFertilizer");
    }

    @Test
    public void bowlsGlassesAndJarsKeepTheirContainers() {
        aliases("category.bowl", "foodTomatoSoup", "foodMozukuSoup");
        aliases("pickup.minecraft.bowl", "foodBerrybowl", "foodQuinoaCereal", "foodStirFry");
        aliases("category.potion", "foodStrawberrySmoothie", "foodHarlequinFizz", "foodJelly", "foodPeanutButter");
        aliases("pickup.minecraft.bread", "foodCornBread", "foodSandwichPBJ", "wrapCorn", "wrapCornRice");
        aliases("category.food_dry", "foodCookedRice", "foodHuckleberryTart", "wrapRice");
    }

    @Test
    public void unusualPlantAndBuildingBlocksDoNotKeepTheirStepSound() {
        aliases(
            "pickup.minecraft.bamboo",
            "bambooMoso",
            "bambooMosoPole",
            "bambooMosoStairs",
            "bambooMosoDoor",
            "bambooMosoLadder",
            "wallBracketBambooMoso");
        aliases("category.crunchy_dry", "cactusGoldenSaguaro");
        aliases("pickup.minecraft.coral_block", "coralElegance", "waterRedSeaFan", "coralFragmentRedSeaFan");
        aliases("pickup.minecraft.mushroom_block", "epiphyteTurkeyTail");
        aliases("category.wood", "wallBracketWoodOak");
        aliases("category.metal_bits", "wallBracketIronCurl");
        // Ordinary plants keep their grass step and the stone bracket its stone; the extra brackets are off in the
        // pack.
        assertFalse(events.has("item.pickup.plantmegapack.flowerAlpineThistle"));
        assertFalse(events.has("item.pickup.plantmegapack.berrybushBlue"));
        assertFalse(events.has("item.pickup.plantmegapack.wallBracketStone"));
        assertFalse(events.has("item.pickup.plantmegapack.wallBracketDiamond"));
    }
}
