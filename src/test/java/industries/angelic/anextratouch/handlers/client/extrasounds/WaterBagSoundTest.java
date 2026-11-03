package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import net.minecraft.nbt.NBTTagCompound;

import org.junit.Test;

public class WaterBagSoundTest {

    private static final String BAG = "Growthcraft|Cellar:grc.waterBag";

    private NBTTagCompound contents(int amount) {
        NBTTagCompound tag = new NBTTagCompound();
        // Same FluidStack NBT written by Growthcraft; no running Forge registry needed.
        NBTTagCompound fluid = new NBTTagCompound();
        fluid.setString("FluidName", "water");
        fluid.setInteger("Amount", amount);
        tag.setTag("Fluid", fluid);
        return tag;
    }

    private net.minecraft.util.ResourceLocation filled(String name, NBTTagCompound tag) {
        return InventorySoundRegistry.filledWaterBag(name, tag, "water"::equals);
    }

    @Test
    public void fillingAndDrainingTheSameBagImmediatelyChangesItsCue() {
        NBTTagCompound tag = new NBTTagCompound();
        assertNull(filled(BAG, tag));
        tag.setTag("Fluid", contents(1).getCompoundTag("Fluid"));
        assertEquals(ExtraSounds.id("item.pickup.Growthcraft|Cellar.grc.waterBag.filled"), filled(BAG, tag));
        tag.getCompoundTag("Fluid")
            .setInteger("Amount", 0);
        assertNull(filled(BAG, tag));
        tag.getCompoundTag("Fluid")
            .setInteger("Amount", 1000);
        assertNotNull(filled(BAG, tag));
        tag.removeTag("Fluid");
        assertNull(filled(BAG, tag));
    }

    @Test
    public void missingInvalidAndUnrelatedFluidTagsStaySilent() {
        assertNull(filled(BAG, null));
        assertNull(filled(BAG, contents(0)));
        assertNull(filled(BAG, contents(-1)));
        assertNull(filled("other:waterBag", contents(1000)));
        assertNull(filled(null, contents(1000)));
        NBTTagCompound tag = contents(1000);
        tag.getCompoundTag("Fluid")
            .setString("FluidName", "aet_missing_test_fluid");
        assertNull(filled(BAG, tag));
        tag.getCompoundTag("Fluid")
            .removeTag("FluidName");
        assertNull(filled(BAG, tag));
        tag.setString("Fluid", "invalid");
        assertNull(filled(BAG, tag));
    }
}
