package industries.angelic.anextratouch.varinstances.configcaches;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import org.junit.Test;

import industries.angelic.anextratouch.Config;

public class ArmorCacheTest {

    private static ArmorCache defaults() {
        ArmorCache cache = new ArmorCache();
        cache.populateSoundRules(new String[0], Config.armorNameRules);
        return cache;
    }

    @Test
    public void registryOverrideWinsOverVanillaMaterialAndMuteRule() throws Exception {
        Item item = new ItemArmor(ItemArmor.ArmorMaterial.IRON, 0, 0).setUnlocalizedName("cultist");
        Method register = Item.itemRegistry.getClass()
            .getDeclaredMethod("addObjectRaw", int.class, String.class, Object.class);
        register.setAccessible(true);
        register.invoke(Item.itemRegistry, 31997, "aet_armor_test:helmet", item);
        ArmorCache cache = new ArmorCache();
        cache.populateSoundRules(new String[] { "aet_armor_test:helmet;CRYSTAL" }, new String[] { "none:cultist" });
        assertEquals("crystal", cache.resolveArmorCategory(new ItemStack(item)));
        cache.populateSoundRules(new String[] { "aet_armor_test:helmet;none" }, new String[0]);
        assertEquals("none", cache.resolveArmorCategory(new ItemStack(item)));
        assertEquals(-1, cache.getArmorPriority("none"));
    }

    @Test
    public void twilightForestCrittersOnTheHeadStaySilentUnlessConfigured() throws Exception {
        Item critter = new Item().setUnlocalizedName("critter");
        Method register = Item.itemRegistry.getClass()
            .getDeclaredMethod("addObjectRaw", int.class, String.class, Object.class);
        register.setAccessible(true);
        register.invoke(Item.itemRegistry, 31996, "TwilightForest:item.critter", critter);
        ArmorCache cache = new ArmorCache();
        cache.populateSoundRules(new String[0], new String[0]);
        assertEquals("none", cache.resolveArmorCategory(new ItemStack(critter)));
        cache.populateSoundRules(new String[] { "TwilightForest:item.critter;light" }, new String[0]);
        assertEquals("light", cache.resolveArmorCategory(new ItemStack(critter)));
    }

    @Test
    public void vanillaMaterialsWinOverNameRules() {
        ArmorCache cache = defaults();
        ItemArmor.ArmorMaterial[] materials = { ItemArmor.ArmorMaterial.CLOTH, ItemArmor.ArmorMaterial.CHAIN,
            ItemArmor.ArmorMaterial.IRON, ItemArmor.ArmorMaterial.GOLD, ItemArmor.ArmorMaterial.DIAMOND };
        String[] categories = { "light", "medium", "heavy", "heavy", "crystal" };
        cache.populateSoundRules(new String[0], new String[] { "none:amethyst" });
        for (int i = 0; i < materials.length; i++) assertEquals(
            categories[i],
            cache
                .resolveArmorCategory(new ItemStack(new ItemArmor(materials[i], 0, 0).setUnlocalizedName("amethyst"))));
    }

    @Test
    public void defaultsMatchBothNamesInEfrOrder() {
        ArmorCache cache = defaults();
        assertEquals("medium", cache.resolveName("item.cultistHelmet", "COPPER"));
        assertEquals("crystal", cache.resolveName("item.amethystHelmet", "GOLD_ALLOY"));
        assertEquals("heavy", cache.resolveName("item.ironwoodHelm", "IRONWOOD"));
        assertEquals("heavy", cache.resolveName("item.steeleafHelm", "STEELEAF"));
        assertEquals("heavy", cache.resolveName("item.helmet", "ASTRAL_SILVER"));
        assertEquals("crystal", cache.resolveName("item.helmet", "MITHRIL"));
        assertEquals("light", cache.resolveName("item.thermal_padding", "UNKNOWN"));
        assertEquals("netherite", cache.resolveName("item.heavyblazeHelmet", "UNKNOWN"));
        assertEquals("netherite", cache.resolveName("item.helmet", "VULCANITE"));
        assertEquals("turtle", cache.resolveName("item.helmet", "WOOD"));
        assertEquals("elytra", cache.resolveName("item.hazmatHelmet", "UNKNOWN"));
        assertNull(cache.resolveName("item.unmappedHelmet", "UNKNOWN"));
    }

    @Test
    public void packArmorNamesAndReportedMaterialsSelectExpectedCategories() {
        ArmorCache cache = defaults();
        assertEquals("crystal", cache.resolveName("item.helmetAmethyst", "AMETHYST"));
        assertEquals("heavy", cache.resolveName("item.EfrineHelmet", "EfrineArmorMaterial"));
        assertEquals("netherite", cache.resolveName("item.HeavyBlazeHelmet", "HeavyBlazeArmorMaterial"));
        // Mowzie's Barakoa mask constructs with CLOTH but reports CHAIN through its getter.
        Item mask = new ItemArmor(ItemArmor.ArmorMaterial.CLOTH, 0, 0) {

            @Override
            public ItemArmor.ArmorMaterial getArmorMaterial() {
                return ItemArmor.ArmorMaterial.CHAIN;
            }
        }.setUnlocalizedName("barakoaMask");
        assertEquals("medium", cache.resolveArmorCategory(new ItemStack(mask)));
        assertEquals(
            "heavy",
            cache.resolveArmorCategory(
                new ItemStack(new ItemArmor(ItemArmor.ArmorMaterial.IRON, 0, 0).setUnlocalizedName("wroughtHelm"))));
        assertEquals(
            "crystal",
            cache.resolveArmorCategory(
                new ItemStack(
                    new ItemArmor(ItemArmor.ArmorMaterial.DIAMOND, 0, 0).setUnlocalizedName("gravitite_helmet"))));
    }

    @Test
    public void muteFirstAndCategoryPriorityDoesNotDependOnListOrder() {
        ArmorCache cache = new ArmorCache();
        cache.populateSoundRules(
            new String[0],
            new String[] { "generic:helmet", "heavy:foo", "medium:foo", "none:mute" });
        assertEquals("none", cache.resolveName("item.diamond_foo_helmet", "MUTE"));
        assertEquals("medium", cache.resolveName("item.diamond_foo_helmet", ""));
        assertEquals("generic", cache.resolveName("item.helmet", ""));
        cache.populateSoundRules(new String[0], new String[] { "heavy:foo", "light:", "bad:foo", "light:foo:bar", "" });
        assertEquals("heavy", cache.resolveName("item.fooHelmet", ""));
        assertNull(cache.resolveName("item.helmet", "MUTE"));
    }

    @Test
    public void caseFoldingIsIndependentOfSystemLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("tr", "TR"));
            ArmorCache cache = new ArmorCache();
            cache.populateSoundRules(new String[0], new String[] { "LIGHT:PADDED" });
            assertEquals("light", cache.resolveName("item.PADDED_Helmet", ""));
            assertEquals("heavy", cache.resolveName("item.helmet", "IRON_CUSTOM"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void unknownEquipmentKeepsConfiguredFallbackAndNewCategoriesCanPlay() {
        ArmorCache cache = defaults();
        assertEquals(
            Config.armorDefaultCategory,
            cache.resolveArmorCategory(new ItemStack(new Item().setUnlocalizedName("unmapped"))));
        assertNull(cache.resolveArmorCategory(null));
        assertEquals(cache.getArmorPriority("heavy"), cache.getArmorPriority("netherite"));
        for (String category : new String[] { "turtle", "generic", "elytra" })
            assertTrue(cache.getArmorPriority(category) >= 0);
    }
}
