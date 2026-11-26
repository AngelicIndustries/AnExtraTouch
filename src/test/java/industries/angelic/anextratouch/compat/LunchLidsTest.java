package industries.angelic.anextratouch.compat;

import static org.junit.Assert.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.Test;

public class LunchLidsTest {

    @Test
    public void theLidIsOpenOnlyWithTheFlagSet() {
        ItemStack stack = new ItemStack(Items.bowl);
        assertFalse(LunchLids.open(null));
        assertFalse(LunchLids.open(stack));
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound()
            .setBoolean("Open", true);
        assertTrue(LunchLids.open(stack));
    }
}
