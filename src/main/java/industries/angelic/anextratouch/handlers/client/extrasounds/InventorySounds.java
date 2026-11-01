package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import industries.angelic.anextratouch.Config;

/** One completed local inventory transaction, including creative operations that bypass windowClick. */
public final class InventorySounds {

    private static int batchDepth;
    private static InventorySounds batchStart;
    private static Cue batchCue;

    public static void beginBatch(EntityPlayer player) {
        if (batchDepth++ == 0) {
            batchCue = null;
            batchStart = new InventorySounds(
                player.inventory,
                player.openContainer,
                null,
                0,
                0,
                0,
                false,
                false,
                false);
        }
    }

    public static void endBatch() {
        if (--batchDepth != 0) return;
        Cue cue = batchCue;
        InventorySounds start = batchStart;
        batchCue = null;
        batchStart = null;
        if (start.changed()) play(cue);
    }

    private final Container container;
    private final InventoryPlayer inventory;
    private final Slot slot;
    private final ItemStack cursor, clicked, hotbar;
    private final List<ItemStack> contents = new ArrayList<>();
    private final int index, button, mode;
    private final boolean creative, palette, trash;
    private final boolean hadInventory;

    public static InventorySounds begin(EntityPlayer player, Container container, Slot slot, int index, int button,
        int mode, boolean creative, boolean palette, boolean trash) {
        if (!Config.extraInventoryClicks && !Config.extraCreativeActions && !Config.extraItemDrops) return null;
        return new InventorySounds(player.inventory, container, slot, index, button, mode, creative, palette, trash);
    }

    InventorySounds(InventoryPlayer inventory, Container container, Slot slot, int index, int button, int mode,
        boolean creative, boolean palette, boolean trash) {
        this.inventory = inventory;
        this.container = container;
        this.slot = slot;
        this.index = index;
        this.button = button;
        this.mode = mode;
        this.creative = creative;
        this.palette = palette;
        this.trash = trash;
        cursor = copy(inventory.getItemStack());
        clicked = slot == null ? null : copy(slot.getStack());
        hotbar = mode == 2 && button >= 0 && button < 9 ? copy(inventory.getStackInSlot(button)) : null;
        boolean any = false;
        for (ItemStack stack : inventory.mainInventory) any |= stack != null;
        for (ItemStack stack : inventory.armorInventory) any |= stack != null;
        hadInventory = any;
        for (Object value : container.inventorySlots) contents.add(copy(((Slot) value).getStack()));
    }

    public void finish() {
        Cue cue = result();
        if (batchDepth > 0) {
            // A mass drop takes priority over its preliminary pickup clicks.
            if (cue != null && (batchCue == null || "item.drop".equals(cue.event))) batchCue = cue;
        } else play(cue);
    }

    private static void play(Cue cue) {
        if (cue != null) ExtraSounds
            .ui(cue.item == null ? ExtraSounds.id(cue.event) : InventorySoundRegistry.sound(cue.item), cue.pitch);
    }

    boolean changed() {
        boolean changed = !ItemStack.areItemStacksEqual(cursor, inventory.getItemStack());
        for (int i = 0; i < contents.size() && !changed; i++) {
            changed = !ItemStack.areItemStacksEqual(
                contents.get(i),
                container.getSlot(i)
                    .getStack());
        }
        if (mode == 2 && button >= 0 && button < 9)
            changed |= !ItemStack.areItemStacksEqual(hotbar, inventory.getStackInSlot(button));
        return changed;
    }

    Cue result() {
        ItemStack after = inventory.getItemStack();
        boolean changed = changed();
        if (creative && trash) {
            return Config.extraCreativeActions && (mode == 1 ? hadInventory : changed)
                ? new Cue(mode == 1 ? "item.delete_all" : "item.delete_partial", null, 1)
                : null;
        }
        // Creative palette drops copy the slot without changing it; the action still succeeded.
        if (creative && mode == 4 && palette && clicked != null) {
            return drop(clicked, button == 0 ? 1 : clicked.getMaxStackSize());
        }
        if (!changed) return null;
        // Drag end uses the outside slot index too, but distributes items instead of dropping them.
        if (mode != 5 && (index == -999 || mode == 4)) {
            ItemStack source = index == -999 || clicked == null ? cursor : clicked;
            ItemStack remainder = index == -999 || clicked == null ? after : slot.getStack();
            return drop(source, count(source) - count(remainder));
        }
        if (mode == 3) {
            return Config.extraCreativeActions && after != null ? new Cue("item.clone", null, 1) : null;
        }
        if (creative && (palette && cursor != null && count(after) < count(cursor)
            || mode == 1 && !palette && clicked != null && (slot == null || slot.getStack() == null))) {
            return Config.extraCreativeActions ? new Cue("item.delete_partial", null, 1) : null;
        }
        if (!Config.extraInventoryClicks) return null;
        // The completed drag places the cursor's item, even when it empties the cursor.
        if (mode == 5) return cursor == null ? null : new Cue(null, cursor, 0.9F);
        if (mode == 6) return new Cue("item.pickup_all", null, 1);
        boolean placing = mode == 0 && cursor != null && (clicked == null || sameItem(cursor, clicked));
        ItemStack sounded = placing ? cursor : clicked != null ? clicked : hotbar;
        return sounded == null ? null : new Cue(null, sounded, placing ? 0.9F : 1.0F);
    }

    private static Cue drop(ItemStack source, int count) {
        if (!Config.extraItemDrops || source == null || count <= 0) return null;
        return new Cue(
            "item.drop",
            null,
            ExtraSounds.dropPitch(count, source.isStackable() ? source.getMaxStackSize() : 1));
    }

    static final class Cue {

        final String event;
        final ItemStack item;
        final float pitch;

        Cue(String event, ItemStack item, float pitch) {
            this.event = event;
            this.item = item;
            this.pitch = pitch;
        }
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        return a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    private static ItemStack copy(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    private static int count(ItemStack stack) {
        return stack == null ? 0 : stack.stackSize;
    }
}
