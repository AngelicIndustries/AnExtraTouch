package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProjectZuluInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            ProjectZuluInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private void aliases(String target, String... registryNames) {
        for (String name : registryNames) {
            String key = "item.pickup.ProjectZulu|Core." + name;
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
    public void customArmorMaterialsFollowWhatTheArmorIsMadeOf() {
        // Every set but the iron, gold and diamond scale ones fell to the generic cue.
        aliases("category.banner", "BlueClothHelmet", "WhiteClothBoots");
        aliases("category.gear.leather", "FurChest");
        aliases("category.crunchy_dry", "CactusLegs");
        aliases("category.gear.armadillo", "ScaleHelmet");
        aliases("category.gear.iron", "IronScaleChest");
        aliases("category.gear.golden", "GoldScaleBoots");
        aliases("category.gear.diamond", "DiamondScaleLegs");
    }

    @Test
    public void craftingDropsGetTheirOwnCuePerMetadata() {
        aliases("pickup.minecraft.bone", "GenericCraftingItems1.1", "GenericCraftingItems1.25");
        aliases("category.meat", "GenericCraftingItems1.28", "GenericCraftingItems1.31", "GenericCraftingItems1.37");
        aliases("pickup.minecraft.slime_ball", "GenericCraftingItems1.39");
        aliases("category.stone", "ThrowingRock", "GenericCraftingItems1.42");
        aliases("category.paper", "StructurePlacer");
        aliases("category.wood", "CoconutShell");
        aliases("category.dust", "GenericCraftingItems1.22", "AloeVeraSeeds");
    }

    @Test
    public void blocksStuckOnTheDefaultStoneSoundFollowTheirMaterial() {
        aliases("category.wood", "palmtreeplank", "palmtreestairs", "spikes_wood", "campfire");
        aliases("category.leaves", "palmtreesapling");
        aliases("pickup.minecraft.bone", "spikes_ivory");
        aliases("category.banner", "mobskulls");
        aliases("category.metal_bits", "brewingstandsingle", "spikes_iron");
        aliases("category.brick", "universalflowerpot", "UniversalFlowerPot");
        // These already set a fitting sound.
        assertFalse(events.has("item.pickup.ProjectZulu|Core.jasper"));
        assertFalse(events.has("item.pickup.ProjectZulu|Core.palmtreelog"));
    }
}
