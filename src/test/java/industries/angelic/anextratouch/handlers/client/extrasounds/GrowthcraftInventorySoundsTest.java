package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GrowthcraftInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            GrowthcraftInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private String key(String module, String name) {
        return "item.pickup.Growthcraft" + (module.isEmpty() ? "" : "|" + module) + "." + name;
    }

    private String target(String module, String name, int metadata) {
        String key = key(module, name);
        if (events.has(key + "." + metadata)) key += "." + metadata;
        assertTrue("Missing case-sensitive registry ID: " + key, events.has(key));
        JsonObject definition = events.getAsJsonObject(key);
        assertEquals(
            "player",
            definition.get("category")
                .getAsString());
        assertEquals(
            1,
            definition.getAsJsonArray("sounds")
                .size());
        JsonObject sound = definition.getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject();
        assertEquals(
            "event",
            sound.get("type")
                .getAsString());
        return sound.get("name")
            .getAsString();
    }

    private void aliases(String module, String name, String family) {
        assertEquals("anextratouch:item." + family, target(module, name, 0));
    }

    @Test
    public void allBoozeBucketRegistrationsHaveLiquidAccents() {
        // Counts and names come from the installed 2.7.3 registration arrays.
        String[][] series = { { "Apples", "grc.appleCider", "grc.appleCider", "7" },
            { "Bees", "grc.honeyMead", "grc.honeyMead", "7" }, { "Grapes", "grc.grapeWine", "grc.grapeWine", "8" },
            { "Hops", "grc.hopAle", "grc.hopAle", "9" }, { "Hops", "grc.lager", "grc.lager", "7" },
            { "Rice", "grc.riceSake", "grc.riceSake", "7" }, { "Milk", "grcmilk.Kumis", "grcmilk.KumisBottle", "6" } };
        for (String[] entry : series) {
            int count = Integer.parseInt(entry[3]);
            for (int variant = 0; variant < count; variant++) {
                String bucket = entry[1] + "Bucket." + variant;
                aliases(entry[0], bucket, "category.bucket");
                aliases(entry[0], bucket + ".accent", "category.bucket_liquid");
                assertEquals("anextratouch:item.category.potion", target(entry[0], entry[2], variant));
            }
            assertFalse(events.has(key(entry[0], entry[2] + ".accent")));
            assertFalse(events.has(key(entry[0], entry[1] + "Bucket." + count)));
        }
    }

    @Test
    public void fluidFactoryNamesUseRegistryNamesNotFoodTranslationNames() {
        for (String name : new String[] { "ButterMilk", "Cream", "Curds", "Rennet", "SkimMilk", "Whey",
            "PasteurizedMilk" }) {
            aliases("Milk", "grcmilk.BottleFluid" + name, "category.potion");
            aliases("Milk", "grcmilk.BucketFluid" + name, "category.bucket");
            aliases("Milk", "grcmilk.BucketFluid" + name + ".accent", "category.bucket_liquid");
        }
        aliases("Milk", "grcmilk.BottleFluidMilk", "category.potion");
        assertFalse(events.has(key("Milk", "grcmilk.FoodBottleFluidMilk")));
        assertFalse(events.has(key("Milk", "grcmilk.BucketFluidMilk"))); // Uses vanilla milk bucket.
        aliases("", "grccore.BottleFluidSaltWater", "category.potion");
        aliases("", "grccore.BucketFluidSaltWater.accent", "category.bucket_liquid");
        aliases("Bees", "grc.BottleFluidHoney", "category.potion");
        aliases("Bees", "grc.BucketFluidHoney.accent", "category.bucket_liquid");
    }

    @Test
    public void drySaltAndNbtWaterBagsDoNotAlwaysSlosh() {
        aliases("", "grccore.salt", "category.dust");
        aliases("", "grccore.saltBottle", "category.bottle_empty");
        aliases("", "grccore.saltBucket", "category.bucket");
        aliases("", "grccore.saltBottle.accent", "category.dust");
        aliases("", "grccore.saltBucket.accent", "category.dust");
        // Bag metadata is color; only the NBT-aware lookup selects the filled event.
        for (int color = 0; color < 17; color++) {
            assertEquals("anextratouch:item.pickup.minecraft.leather", target("Cellar", "grc.waterBag", color));
            assertFalse(events.has(key("Cellar", "grc.waterBag." + color + ".accent")));
        }
        assertFalse(events.has(key("Cellar", "grc.waterBag.accent")));
        aliases("Cellar", "grc.waterBag.filled", "pickup.Growthcraft|Cellar.grc.waterBag");
        aliases("Cellar", "grc.waterBag.filled.accent", "category.bucket_liquid");
    }

    @Test
    public void produceDairyAndCulturesUseTheirMaterialsAcrossVariants() {
        for (int grape = 0; grape < 3; grape++) {
            assertEquals("anextratouch:item.category.fruit", target("Grapes", "grc.grapes", grape));
        }
        for (int flavor = 0; flavor < 6; flavor++) {
            assertEquals("anextratouch:item.pickup.minecraft.bowl", target("Milk", "grcmilk.Yogurt", flavor));
            assertEquals("anextratouch:item.pickup.minecraft.bowl", target("Milk", "grcmilk.IceCream", flavor));
        }
        aliases("Milk", "grcmilk.Cheese", "pickup.minecraft.bread");
        aliases("Milk", "grcmilk.Butter", "pickup.minecraft.bread");
        aliases("Milk", "grcmilk.StarterCulture", "category.dust");
        aliases("Milk", "grcmilk.Stomach", "category.meat");
        aliases("Cellar", "grc.yeast", "category.dust");
        aliases("Rice", "grc.rice", "category.hay");
        aliases("Rice", "grc.riceBall", "pickup.minecraft.bread");
        aliases("Bees", "grc.honeyJar", "category.bowl"); // Clay pot, not a glass bottle.
    }

    @Test
    public void blocksAndToolsRetainTheirDistinctMaterials() {
        aliases("Milk", "grcmilk.CheeseCloth", "category.banner");
        aliases("Milk", "grcmilk.HangingCurds", "category.banner");
        aliases("Milk", "grcmilk.Pancheon", "category.brick");
        aliases("Milk", "grcmilk.CheeseVat", "category.bucket");
        aliases("Cellar", "grc.brewKettle", "category.bucket");
        aliases("Cellar", "grc.fermentJar", "category.bottle_empty");
        aliases("", "grccore.crowbar", "category.gear.iron");
        aliases("Bamboo", "grc.bambooRaft", "category.boat");
        aliases("Bamboo", "grc.bambooFence", "pickup.minecraft.bamboo");
        aliases("Bamboo", "grc.bambooShootFood", "category.crunchy_dry");
        assertFalse(events.has(key("Bamboo", "grc.bambooLeaves")));
        assertFalse(events.has(key("Milk", "grcmilk.ButterChurn"))); // Already wood.
        assertFalse(events.has(key("Cellar", "grc.chievItemDummy")));
    }
}
