package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import industries.angelic.anextratouch.Config;

public class KitchenItemSoundsTest {

    private final InventoryBasic rack = new InventoryBasic("rack", true, 2);
    private final Item tool = new Item();

    @Before
    @After
    public void config() {
        Config.extraInventoryClicks = true;
    }

    @Test
    public void emptyAndRejectedClicksStaySilent() {
        assertNull(new KitchenItemSounds(rack).result());
        rack.setInventorySlotContents(0, new ItemStack(tool));
        assertNull(new KitchenItemSounds(rack).result());
    }

    @Test
    public void insertionAndRemovalUseTheActualItemAndInventoryPitch() {
        KitchenItemSounds insert = new KitchenItemSounds(rack);
        rack.setInventorySlotContents(1, new ItemStack(tool));
        assertSame(tool, insert.result().item.getItem());
        assertEquals(0.9F, insert.result().pitch, 0);
        KitchenItemSounds remove = new KitchenItemSounds(rack);
        rack.setInventorySlotContents(1, null);
        assertSame(tool, remove.result().item.getItem());
        assertEquals(1.0F, remove.result().pitch, 0);
    }

    @Test
    public void swapUsesOnePlacementCueForTheReplacementIncludingItsNbt() {
        rack.setInventorySlotContents(0, new ItemStack(tool));
        KitchenItemSounds swap = new KitchenItemSounds(rack);
        ItemStack replacement = new ItemStack(new Item());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("material", "wood");
        replacement.setTagCompound(tag);
        rack.setInventorySlotContents(0, replacement);
        assertSame(replacement, swap.result().item);
        assertEquals(0.9F, swap.result().pitch, 0);
    }

    @Test
    public void snapshotDetectsInPlaceMutationsAndToggleOffSuppressesThem() {
        rack.setInventorySlotContents(0, new ItemStack(tool, 2));
        KitchenItemSounds take = new KitchenItemSounds(rack);
        rack.getStackInSlot(0).stackSize--;
        assertEquals(2, take.result().item.stackSize);
        assertEquals(1.0F, take.result().pitch, 0);
        Config.extraInventoryClicks = false;
        assertNull(take.result());
    }

    @Test
    public void ovenToolSlotsAreCoveredBeyondTheFirstTwoSlots() {
        InventoryBasic oven = new InventoryBasic("oven", true, 20);
        KitchenItemSounds insert = new KitchenItemSounds(oven);
        oven.setInventorySlotContents(19, new ItemStack(tool));
        assertSame(tool, insert.result().item.getItem());
        assertEquals(0.9F, insert.result().pitch, 0);
    }
}
