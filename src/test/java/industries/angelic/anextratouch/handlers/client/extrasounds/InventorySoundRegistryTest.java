package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import industries.angelic.anextratouch.handlers.client.ItemSoundRegistry;

public class InventorySoundRegistryTest {

    private static int nextId = 31800;

    @Before
    @After
    public void resetOverrides() {
        ItemSoundRegistry.reload(new String[0]);
    }

    private ItemStack stack(Item item) throws Exception {
        Method add = Item.itemRegistry.getClass()
            .getDeclaredMethod("addObjectRaw", int.class, String.class, Object.class);
        add.setAccessible(true);
        int id = nextId++;
        add.invoke(Item.itemRegistry, id, "aet_inventory_test:item" + id, item);
        return new ItemStack(item);
    }

    @Test
    public void generatedItemFamiliesUseDefinedCategoriesInsteadOfGenericClicks() throws Exception {
        Item[] items = { new ItemBoat(), new ItemMinecart(0), new ItemSaddle(), new ItemLead(), new ItemEmptyMap() {},
            new ItemDye(), new ItemSoup(6), new ItemBucket(null), new ItemBow(), new ItemFishingRod() };
        JsonObject definitions;
        try (InputStreamReader reader = new InputStreamReader(
            getClass().getResourceAsStream("/assets/anextratouch/sounds.json"),
            StandardCharsets.UTF_8)) {
            definitions = new JsonParser().parse(reader)
                .getAsJsonObject();
        }
        for (Item item : items) {
            ResourceLocation sound = InventorySoundRegistry.fallback(stack(item));
            assertNotEquals(
                item.getClass()
                    .getName(),
                ExtraSounds.id("item.pickup"),
                sound);
            assertTrue(
                "Missing sound for " + item.getClass()
                    .getName() + ": " + sound,
                definitions.has(sound.getResourcePath()));
        }
    }

    @Test
    public void hoesUseTheirOwnMaterialRatherThanAlwaysIron() throws Exception {
        assertEquals(
            ExtraSounds.id("item.category.gear.wood"),
            InventorySoundRegistry.fallback(stack(new ItemHoe(Item.ToolMaterial.WOOD))));
        assertEquals(
            ExtraSounds.id("item.category.gear.diamond"),
            InventorySoundRegistry.fallback(stack(new ItemHoe(Item.ToolMaterial.EMERALD))));
    }

    @Test
    public void explicitClassifierOverridesStillWinForNewFamilies() throws Exception {
        ItemStack boat = stack(new ItemBoat());
        String name = Item.itemRegistry.getNameForObject(boat.getItem());
        ItemSoundRegistry.reload(new String[] { name + "=none" });
        assertEquals(ExtraSounds.id("muted"), InventorySoundRegistry.fallback(boat));
        ItemSoundRegistry.reload(new String[] { name + "=book" });
        assertEquals(ExtraSounds.id("item.category.paper"), InventorySoundRegistry.fallback(boat));
    }
}
