package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import industries.angelic.anextratouch.Config;

public class InventorySoundsTest {

    private final Item item = new Item();
    private final InventoryPlayer player = new InventoryPlayer(null);
    private final InventoryBasic slots = new InventoryBasic("test", true, 2);
    private final Container container = new Container() {

        {
            addSlotToContainer(new Slot(slots, 0, 0, 0));
            addSlotToContainer(new Slot(slots, 1, 0, 0));
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return true;
        }
    };

    @Before
    @After
    public void config() {
        Config.extraInventoryClicks = Config.extraCreativeActions = Config.extraItemDrops = true;
    }

    private InventorySounds begin(int index, int button, int mode, boolean creative, boolean palette, boolean trash) {
        return new InventorySounds(
            player,
            container,
            index < 0 ? null : container.getSlot(index),
            index,
            button,
            mode,
            creative,
            palette,
            trash);
    }

    private ItemStack stack(int count) {
        return new ItemStack(item, count);
    }

    @Test
    public void rejectedClicksAndUnchangedShiftTransfersAreSilent() {
        slots.setInventorySlotContents(0, stack(20));
        assertNull(begin(0, 0, 0, false, false, false).result());
        assertNull(begin(0, 0, 1, false, false, false).result());
    }

    @Test
    public void pickupAndPlaceUseDifferentPitch() {
        slots.setInventorySlotContents(0, stack(20));
        InventorySounds pickup = begin(0, 0, 0, false, false, false);
        player.setItemStack(slots.getStackInSlot(0));
        slots.setInventorySlotContents(0, null);
        assertEquals(1, pickup.result().pitch, 0);
        InventorySounds place = begin(1, 0, 0, false, false, false);
        slots.setInventorySlotContents(1, player.getItemStack());
        player.setItemStack(null);
        assertEquals(0.9, place.result().pitch, 0.00001);
    }

    @Test
    public void dragEndOutsideIsNotADropAndEmptyDragIsSilent() {
        player.setItemStack(stack(20));
        assertNull(begin(-999, 0, 5, false, false, false).result());
        InventorySounds drag = begin(-999, 2, 5, false, false, false);
        slots.setInventorySlotContents(0, stack(10));
        player.setItemStack(stack(10));
        assertEquals("item.drag", drag.result().event);
    }

    @Test
    public void outsideDropUsesOnlyTheRemovedCount() {
        player.setItemStack(stack(20));
        InventorySounds drop = begin(-999, 1, 0, false, false, false);
        player.setItemStack(stack(19));
        assertEquals("item.drop", drop.result().event);
        assertEquals(2, drop.result().pitch, 0);
    }

    @Test
    public void collectAndHotbarSwapDoNotNeedCursorReplacement() {
        player.setItemStack(stack(20));
        slots.setInventorySlotContents(0, stack(10));
        InventorySounds collect = begin(0, 0, 6, false, false, false);
        player.getItemStack().stackSize = 30;
        slots.setInventorySlotContents(0, null);
        assertEquals("item.pickup_all", collect.result().event);
        player.setItemStack(null);
        player.setInventorySlotContents(0, stack(12));
        InventorySounds swap = begin(1, 0, 2, false, false, false);
        slots.setInventorySlotContents(1, player.getStackInSlot(0));
        player.setInventorySlotContents(0, null);
        assertEquals(12, swap.result().item.stackSize);
    }

    @Test
    public void creativePaletteDropsSoundWithoutChangingThePalette() {
        slots.setInventorySlotContents(0, stack(1));
        InventorySounds one = begin(0, 0, 4, true, true, false);
        InventorySounds all = begin(0, 1, 4, true, true, false);
        assertEquals(2, one.result().pitch, 0);
        assertEquals(1.5, all.result().pitch, 0.00001);
        Config.extraItemDrops = false;
        assertNull(all.result());
    }

    @Test
    public void creativeDeleteAllIsPacketOnlyAndIndependentOfClicks() {
        player.setInventorySlotContents(0, stack(10));
        Config.extraInventoryClicks = false;
        InventorySounds delete = begin(0, 0, 1, true, false, true);
        assertEquals("item.delete_all", delete.result().event);
        Config.extraCreativeActions = false;
        assertNull(delete.result());
    }

    @Test
    public void creativeCloneAndCursorDeletionHaveTheirOwnEvents() {
        slots.setInventorySlotContents(0, stack(1));
        InventorySounds clone = begin(0, 2, 3, true, true, false);
        player.setItemStack(stack(64));
        assertEquals("item.clone", clone.result().event);
        InventorySounds delete = begin(0, 1, 0, true, true, false);
        player.setItemStack(stack(63));
        assertEquals("item.delete_partial", delete.result().event);
    }
}
