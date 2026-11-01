package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BopInventorySoundsTest {

    private static JsonObject events;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            BopInventorySoundsTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            events = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private String target(String item, int metadata) {
        // The registry name is case-sensitive, and subtype definitions precede the base.
        String key = "item.pickup.BiomesOPlenty." + item;
        if (events.has(key + "." + metadata)) key += "." + metadata;
        if (!events.has(key)) return null;
        JsonObject definition = events.getAsJsonObject(key);
        assertEquals(
            "player",
            definition.get("category")
                .getAsString());
        return definition.getAsJsonArray("sounds")
            .get(0)
            .getAsJsonObject()
            .get("name")
            .getAsString();
    }

    @Test
    public void foodSubtypesDistinguishPowderProduceAndBowls() {
        assertEquals("anextratouch:item.pickup.minecraft.sweet_berries", target("food", 0));
        assertEquals("anextratouch:item.category.dust", target("food", 1));
        assertEquals("anextratouch:item.pickup.minecraft.carrot", target("food", 2));
        for (int meta : new int[] { 3, 8, 12 })
            assertEquals("anextratouch:item.pickup.minecraft.apple", target("food", meta));
        for (int meta : new int[] { 4, 5, 6, 13 })
            assertEquals("anextratouch:item.pickup.minecraft.bowl", target("food", meta));
        assertEquals("anextratouch:item.category.potion", target("food", 10));
        assertEquals("anextratouch:item.pickup.minecraft.honeycomb", target("food", 9));
        assertEquals("anextratouch:item.pickup.minecraft.carrot", target("food", 11));
    }

    @Test
    public void craftingMaterialsDoNotCollapseToOneClick() {
        assertEquals("anextratouch:item.category.brick", target("misc", 0));
        assertEquals("anextratouch:item.category.meat", target("misc", 3));
        assertEquals("anextratouch:item.pickup.minecraft.amethyst_shard", target("misc", 4));
        for (int meta : new int[] { 1, 5, 6, 7, 8, 9, 11 })
            assertEquals("anextratouch:item.category.dust", target("misc", meta));
        assertEquals("anextratouch:item.category.wood", target("misc", 13));
        assertEquals("anextratouch:item.pickup.minecraft.amethyst_shard", target("gems", 0));
        for (int meta = 1; meta < 8; meta++)
            assertEquals("anextratouch:item.pickup.minecraft.diamond", target("gems", meta));
    }

    @Test
    public void containersAndToolsUseTheirMaterials() {
        assertEquals("anextratouch:item.category.bottle_empty", target("jarEmpty", 0));
        assertEquals("anextratouch:item.pickup.minecraft.honey_bottle", target("jarFilled", 0));
        assertEquals("anextratouch:item.category.potion", target("jarFilled", 1));
        assertEquals("anextratouch:item.category.bottle_empty", target("jarFilled", 2));
        assertEquals("anextratouch:item.category.bucket", target("bopBucket", 0));
        assertEquals("anextratouch:item.category.arrow", target("dart", 1));
        for (String part : new String[] { "sword", "shovel", "pickaxe", "axe", "hoe", "scythe", "helmet", "chestplate",
            "leggings", "boots" }) {
            assertEquals("anextratouch:item.category.gear.amethyst", target(part + "Amethyst", 0));
            assertEquals("anextratouch:item.category.gear.mud", target(part + "Mud", 0));
        }
        assertEquals("anextratouch:item.category.gear.wood", target("scytheWood", 0));
        assertEquals("anextratouch:item.category.gear.iron", target("scytheIron", 0));
        assertEquals("anextratouch:item.category.gear.leather", target("wadingBoots", 0));
    }

    @Test
    public void specialBlocksReuseModernMaterialsWithoutChangingOrdinaryStoneAndWood() {
        assertEquals("anextratouch:item.pickup.minecraft.mud", target("mud", 0));
        assertNull(target("mud", 1)); // Quicksand keeps its sand sound.
        assertEquals("anextratouch:item.pickup.minecraft.amethyst_block", target("gemOre", 1));
        assertNull(target("gemOre", 0)); // Ore keeps its stone sound.
        assertNull(target("gemOre", 3)); // Other solid gem blocks match vanilla diamond blocks.
        assertEquals("anextratouch:item.pickup.minecraft.cherry_planks", target("logs1", 1));
        assertNull(target("logs1", 0));
        assertNull(target("rocks", 0));
        assertEquals("anextratouch:item.pickup.minecraft.coral_block", target("coral1", 12));
        assertEquals("anextratouch:item.category.leaves", target("coral1", 11));
        assertEquals("anextratouch:item.category.hay", target("planks", 10));
        assertEquals("anextratouch:item.category.wood", target("hive", 1));
        assertEquals("anextratouch:item.pickup.minecraft.honey_block", target("hive", 3));
    }
}
