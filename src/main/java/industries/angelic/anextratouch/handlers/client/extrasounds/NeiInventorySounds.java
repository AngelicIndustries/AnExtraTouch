package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import industries.angelic.anextratouch.Config;

/** NEI requests often change slots only after the server replies. */
public final class NeiInventorySounds {

    private static final List<Change> pending = new ArrayList<>();
    private static int deletionDepth;

    private NeiInventorySounds() {}

    public static Change pickup(ItemStack stack) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null || stack == null || !Config.extraInventoryClicks) return null;
        ItemStack item = stack.copy();
        return new Change(player, () -> count(player, item), item, false, false);
    }

    public static Change placement(Slot slot, ItemStack stack) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null || slot == null || stack == null || !Config.extraInventoryClicks) return null;
        ItemStack item = stack.copy();
        return new Change(player, () -> count(slot.getStack(), item), item, true, false);
    }

    public static Change delete(int slot, ItemStack type, boolean all) {
        if (deletionDepth++ != 0) return null;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null || !Config.extraCreativeActions) return null;
        Container container = player.openContainer;
        ItemStack item = type == null ? null : type.copy();
        IntSupplier remaining;
        if (all) remaining = () -> count(player, null);
        else if (item != null) remaining = () -> {
            int count = 0;
            for (Object value : container.inventorySlots) {
                ItemStack current = ((Slot) value).getStack();
                // NEI's delete-by-type deliberately ignores NBT.
                if (current != null && current.isItemEqual(item)) count += Math.max(0, current.stackSize);
            }
            return count;
        };
        else if (slot == -999) remaining = () -> count(player.inventory.getItemStack(), null);
        else if (slot >= 0 && slot < container.inventorySlots.size()) remaining = () -> count(
            container.getSlot(slot)
                .getStack(),
            null);
        else return null;
        return new Change(player, remaining, null, false, all);
    }

    public static void endDelete(Change change, boolean completed) {
        deletionDepth--;
        if (completed) submit(change);
    }

    public static void submit(Change change) {
        if (change == null || change.item == null && change.before == 0) return;
        // Shift-trash deletes the cursor and matching slots through separate calls.
        // Keep those requests together even if their replies arrive on different ticks.
        if (change.item == null) {
            for (Change queued : pending) {
                if (queued.item == null && queued.player == change.player
                    && queued.container == change.container
                    && queued.createdTick == change.createdTick) {
                    queued.related.add(change);
                    queued.all |= change.all;
                    return;
                }
            }
        }
        if (pending.size() >= 64) pending.remove(0);
        pending.add(change);
    }

    public static void tick(EntityPlayer player) {
        Iterator<Change> it = pending.iterator();
        while (it.hasNext()) {
            Change change = it.next();
            if (player != change.player || player.openContainer != change.container
                || System.nanoTime() - change.created > 2000000000L) {
                it.remove();
            } else if (change.confirmed()) {
                it.remove();
                if (change.item == null) {
                    if (Config.extraCreativeActions)
                        ExtraSounds.ui(change.all ? "item.delete_all" : "item.delete_partial");
                } else if (Config.extraInventoryClicks) {
                    ExtraSounds.ui(InventorySoundRegistry.sound(change.item), change.placing ? 0.9F : 1.0F);
                }
            }
        }
    }

    private static int count(EntityPlayer player, ItemStack item) {
        int count = count(player.inventory.getItemStack(), item);
        for (ItemStack stack : player.inventory.mainInventory) count += count(stack, item);
        for (ItemStack stack : player.inventory.armorInventory) count += count(stack, item);
        for (Object value : player.openContainer.inventorySlots) {
            Slot slot = (Slot) value;
            if (slot.inventory != player.inventory) count += count(slot.getStack(), item);
        }
        return count;
    }

    private static int count(ItemStack stack, ItemStack item) {
        return stack != null
            && (item == null || stack.isItemEqual(item) && ItemStack.areItemStackTagsEqual(stack, item))
                ? Math.max(0, stack.stackSize)
                : 0;
    }

    public static final class Change {

        private final EntityPlayer player;
        private final Container container;
        private final IntSupplier quantity;
        private final ItemStack item;
        private final int before, createdTick;
        private final long created = System.nanoTime();
        private final boolean placing;
        private boolean all;
        private final List<Change> related = new ArrayList<>();

        Change(EntityPlayer player, IntSupplier quantity, ItemStack item, boolean placing, boolean all) {
            this.player = player;
            this.container = player == null ? null : player.openContainer;
            this.createdTick = player == null ? 0 : player.ticksExisted;
            this.quantity = quantity;
            this.before = quantity.getAsInt();
            this.item = item;
            this.placing = placing;
            this.all = all;
        }

        boolean confirmed() {
            int after = quantity.getAsInt();
            if (item == null ? after < before : after > before) return true;
            for (Change change : related) if (change.confirmed()) return true;
            return false;
        }
    }
}
