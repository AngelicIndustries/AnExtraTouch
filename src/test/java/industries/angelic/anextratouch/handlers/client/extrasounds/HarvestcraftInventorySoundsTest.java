package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HarvestcraftInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            HarvestcraftInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private JsonObject sound(String registryName) {
        String key = "item.pickup.harvestcraft." + registryName;
        assertTrue("Missing case-sensitive registration: " + key, events.has(key));
        JsonObject definition = events.getAsJsonObject(key);
        assertEquals(
            "player",
            definition.get("category")
                .getAsString());
        assertEquals(
            1,
            definition.getAsJsonArray("sounds")
                .size());
        return definition.getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            JsonObject sound = sound(name);
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
    public void dryBowlsAndThickFoodsDoNotSloshLikeSoup() {
        aliases(
            "pickup.minecraft.bowl",
            "fruitsaladItem",
            "friedriceItem",
            "bamboosteamedriceItem",
            "beansandriceItem",
            "plainyogurtItem",
            "icecreamItem",
            "mashedpotatoesItem");
        aliases("category.bowl", "carrotsoupItem", "chickennoodlesoupItem", "stockItem", "meatystewItem", "coffeeItem");
        aliases("pickup.minecraft.bread", "applejellysandwichItem", "pumpkinbreadItem");
    }

    @Test
    public void glassDrinksAndWoodenBucketsHaveDifferentLiquidCues() {
        aliases(
            "category.potion",
            "applejuiceItem",
            "applesmoothieItem",
            "chocolatemilkshakeItem",
            "cherrysodaItem",
            "applejellyItem",
            "royaljellyItem");
        aliases("category.wood", "freshwaterItem", "freshmilkItem", "boiledwaterItem");
        aliases("category.bucket_liquid", "freshwaterItem.accent", "freshmilkItem.accent", "boiledwaterItem.accent");
        aliases("category.bottle_empty", "juicerItem");
        assertFalse(events.has("item.pickup.harvestcraft.applejuiceItem.accent"));
        assertFalse(events.has("item.pickup.harvestcraft.potItem.accent"));
    }

    @Test
    public void produceIngredientsAndMeatFollowTheirVanillaFamilies() {
        aliases("category.fruit", "pearItem", "strawberryItem", "tomatoItem");
        aliases("category.crunchy_dry", "parsnipItem", "cucumberItem");
        aliases("category.dust", "flourItem", "saltItem", "groundcinnamonItem", "strawberryseedItem");
        aliases("category.hay", "riceItem", "barleyItem");
        aliases("category.meat", "muttonrawItem", "muttoncookedItem", "tunarawItem", "shrimprawItem");
        aliases("pickup.minecraft.honeycomb", "honeycombItem", "waxcombItem");
    }

    @Test
    public void smallMushroomsUseTheVanillaGrassStepTuning() {
        for (String item : new String[] { "whitemushroomItem", "grilledmushroomItem" }) {
            JsonObject sound = sound(item);
            assertEquals(
                "minecraft:block.grass.step",
                sound.get("name")
                    .getAsString());
            assertEquals(
                0.6F,
                sound.get("volume")
                    .getAsFloat(),
                0.0001F);
            assertEquals(
                1.7F,
                sound.get("pitch")
                    .getAsFloat(),
                0.0001F);
        }
    }

    @Test
    public void cookwareAndBlockFormsDoNotFallBackToStone() {
        aliases("category.bucket", "potItem", "skilletItem", "saucepanItem", "pot");
        aliases("category.wood", "cuttingboardItem", "mixingbowlItem", "cuttingboard");
        aliases(
            "category.gear.leather",
            "hardenedleatherhelmItem",
            "hardenedleatherchestItem",
            "hardenedleatherleggingsItem",
            "hardenedleatherbootsItem");
        aliases("pickup.minecraft.bread", "carrotcakeItem", "pamcarrotCake", "pampineappleupsidedownCake");
        aliases("pickup.minecraft.honeycomb", "pamcandleDeco1", "pamcandleDeco16");
        assertFalse(events.has("item.pickup.harvestcraft.market"));
        assertFalse(events.has("item.pickup.harvestcraft.pamappleSapling"));
    }

    @Test
    public void waterContainersKeepTheirMaterialsAndShareTheBucketLiquidAccent() {
        aliases("category.wood", "churn", "churnon", "sink.0");
        assertEquals(
            "minecraft:block.stone.hit",
            sound("sink.1").get("name")
                .getAsString());
        aliases("category.brick", "sink.2", "sink.3");
        for (int metadata = 0; metadata < 4; metadata++) {
            aliases("category.bucket_liquid", "sink." + metadata + ".accent");
        }
        assertFalse(events.has("item.pickup.harvestcraft.churn.accent"));
        aliases("category.fruit", "gummybearsItem");
    }

}
