package industries.angelic.anextratouch.handlers.client.extrasounds;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import industries.angelic.anextratouch.Config;

/** Direct kitchen interactions bypass the ordinary container click handler. */
public final class KitchenItemSounds {

    private final IInventory inventory;
    private final ItemStack[] before;

    public KitchenItemSounds(IInventory inventory) {
        this.inventory = inventory;
        before = new ItemStack[inventory.getSizeInventory()];
        for (int i = 0; i < before.length; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            before[i] = stack == null ? null : stack.copy();
        }
    }

    public void finish() {
        InventorySounds.Cue cue = result();
        if (cue != null) ExtraSounds.ui(InventorySoundRegistry.sound(cue.item), cue.pitch);
    }

    InventorySounds.Cue result() {
        if (!Config.extraInventoryClicks) return null;
        for (int i = 0; i < before.length; i++) {
            ItemStack old = before[i], now = inventory.getStackInSlot(i);
            if (ItemStack.areItemStacksEqual(old, now)) continue;
            boolean taking = now == null || old != null && old.isItemEqual(now)
                && ItemStack.areItemStackTagsEqual(old, now)
                && now.stackSize < old.stackSize;
            // A swap gets one placement cue for the new tool, not two overlapping sounds.
            return new InventorySounds.Cue(null, taking ? old : now, taking ? 1.0F : 0.9F);
        }
        return null;
    }
}
