package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.Test;

public class BackpackSoundStateTest {

    @Test
    public void firstSyncAndChangingContentsAreSilentButEquipAndUnequipAreNot() {
        BackpackSoundState state = new BackpackSoundState();
        ItemStack pack = new ItemStack(new Item());
        assertNull(state.changed(pack));
        pack.setTagCompound(new NBTTagCompound());
        pack.getTagCompound()
            .setInteger("fuel", 100);
        assertNull(state.changed(pack));
        pack.getTagCompound()
            .setInteger("fuel", 90);
        assertNull(state.changed(pack));
        assertEquals(
            pack.getItem(),
            state.changed(null)
                .getItem());
        assertNull(state.changed(null));
        assertEquals(
            pack.getItem(),
            state.changed(pack)
                .getItem());
    }

    @Test
    public void emptyInitialSyncAndReplacingPackAreHandled() {
        BackpackSoundState state = new BackpackSoundState();
        assertNull(state.changed(null));
        ItemStack pack = new ItemStack(new Item());
        assertSame(pack, state.changed(pack));
        ItemStack powered = new ItemStack(new Item());
        assertSame(powered, state.changed(powered));
        assertNull(state.changed(powered.copy()));
    }

    @Test
    public void transferWaitsForBothAmountAndContainerResultAndOnlySoundsOnce() {
        BackpackSoundState.Transfer transfer = new BackpackSoundState.Transfer(1000, 1, 1000, false, 0);
        assertNull(transfer.confirm(2000, false, 1));
        assertNull(transfer.confirm(1000, true, 2));
        assertEquals("backpack.tank.fill", transfer.confirm(2000, true, 3));
        assertNull(transfer.confirm(2000, true, 4));
    }

    @Test
    public void boilerConsumptionAndWrongDirectionDoNotConfirmContainerTransfers() {
        BackpackSoundState.Transfer drain = new BackpackSoundState.Transfer(2000, -1, 1000, false, 0);
        assertNull(drain.confirm(1900, true, 1));
        assertNull(drain.confirm(3000, true, 2));
        assertNull(drain.confirm(1000, false, 3));
        assertEquals("backpack.tank.drain", drain.confirm(1000, true, 4));
    }

    @Test
    public void discardNeedsANonemptyTankToActuallyBecomeEmpty() {
        BackpackSoundState.Transfer drain = new BackpackSoundState.Transfer(2000, -1, 1, true, 0);
        assertNull(drain.confirm(1000, true, 1));
        assertEquals("backpack.tank.empty", drain.confirm(0, false, 2));
        assertNull(new BackpackSoundState.Transfer(0, -1, 1, true, 0).confirm(0, true, 1));
    }

    @Test
    public void damageAndCountChangesDoNotSoundLikeEquipping() {
        Item item = new Item().setMaxDamage(100);
        BackpackSoundState state = new BackpackSoundState();
        assertNull(state.changed(new ItemStack(item, 1, 0)));
        assertNull(state.changed(new ItemStack(item, 1, 80)));
        assertNull(state.changed(new ItemStack(item, 2, 80)));
    }

    @Test
    public void expiredRequestsDoNotSoundFromLaterUnrelatedChanges() {
        BackpackSoundState.Transfer transfer = new BackpackSoundState.Transfer(0, 1, 1000, false, 0);
        assertNull(transfer.confirm(1000, true, 2000000000L));
        assertTrue(transfer.expired(2000000000L));
    }
}
