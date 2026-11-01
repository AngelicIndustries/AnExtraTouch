package industries.angelic.anextratouch.compat;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import net.minecraft.util.ResourceLocation;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;

public class EfrInventoryCompatTest {

    private static JsonObject definitions;

    @BeforeClass
    public static void definitions() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(
            EfrInventoryCompatTest.class.getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            definitions = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
    }

    private ResourceLocation sound(String name, int metadata) {
        return EfrInventoryCompat.itemSound(name, metadata, id -> definitions.has(id.getResourcePath()));
    }

    @Test
    public void modernDefinitionsAreReusedAcrossNamespacesAndLegacyNames() {
        assertEquals(ExtraSounds.id("item.pickup.minecraft.amethyst_shard"), sound("etfuturum:amethyst_shard", 0));
        assertEquals(ExtraSounds.id("item.pickup.minecraft.mutton"), sound("etfuturum:mutton_raw", 0));
        assertEquals(ExtraSounds.id("item.pickup.minecraft.iron_nugget"), sound("etfuturum:nugget_iron", 0));
        assertEquals(ExtraSounds.id("item.pickup.minecraft.glow_berries"), sound("etfuturum:glow_berries_item", 0));
        assertEquals(ExtraSounds.id("item.pickup.minecraft.honey_bottle"), sound("etfuturum:honey_bottle", 0));
    }

    @Test
    public void subtypesTranslateToSeparateModernIdentities() {
        assertEquals(ExtraSounds.id("item.pickup.minecraft.raw_copper"), sound("etfuturum:raw_ore", 0));
        assertEquals(ExtraSounds.id("item.pickup.minecraft.raw_iron"), sound("etfuturum:raw_ore", 1));
        assertEquals(ExtraSounds.id("item.pickup.minecraft.raw_gold"), sound("etfuturum:raw_ore", 2));
        assertEquals("white_dye", EfrInventoryCompat.modernName("dye", 0));
        assertEquals("black_dye", EfrInventoryCompat.modernName("dye", 3));
        assertEquals("raw_ore", EfrInventoryCompat.modernName("raw_ore", -1));
        assertEquals("dye", EfrInventoryCompat.modernName("dye", 100));
    }

    @Test
    public void generatedFamiliesCoverBackportsWithoutVanillaInheritance() {
        assertEquals(ExtraSounds.id("item.category.boat"), sound("etfuturum:bop_cherry_chest_boat", 0));
        assertEquals(ExtraSounds.id("item.category.gear.leather"), sound("etfuturum:elytra", 0));
        assertEquals(ExtraSounds.id("item.category.gear.netherite"), sound("etfuturum:netherite_spade", 0));
        assertEquals(ExtraSounds.id("item.category.gear.netherite"), sound("etfuturum:netherite_chestplate", 0));
        assertEquals(ExtraSounds.id("item.category.banner"), sound("etfuturum:banner", 15));
        assertEquals(ExtraSounds.id("item.category.banner"), sound("etfuturum:white_bed", 0));
        assertEquals(ExtraSounds.id("item.category.arrow"), sound("etfuturum:tipped_arrow", 42));
        assertEquals(ExtraSounds.id("item.category.dust"), sound("etfuturum:dye", 0));
    }

    @Test
    public void modernPackOverridesWinOverGeneratedFamilies() {
        ResourceLocation modern = ExtraSounds.id("item.pickup.minecraft.netherite_shovel");
        assertEquals(
            modern,
            EfrInventoryCompat.itemSound(
                "etfuturum:netherite_spade",
                0,
                id -> id.equals(modern) || definitions.has(id.getResourcePath())));
        ResourceLocation subtype = ExtraSounds.id("item.pickup.minecraft.black_dye.3");
        assertEquals(
            subtype,
            EfrInventoryCompat
                .itemSound("etfuturum:dye", 3, id -> id.equals(subtype) || definitions.has(id.getResourcePath())));
    }

    @Test
    public void unrelatedModsAndUnknownItemsKeepTheirExistingFallbacks() {
        assertNull(sound("anothermod:amethyst_shard", 0));
        assertNull(sound("minecraft:amethyst_shard", 0));
        assertNull(sound("etfuturum:debugging_tool", 0));
        assertNull(sound(null, 0));
    }
}
