package industries.angelic.anextratouch.compat;

import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/** Tank contents through Forge's fluid API, and the device behind a Growthcraft cellar button packet. */
public final class FluidTanks {

    private FluidTanks() {}

    /** A Growthcraft button packet's device; its coordinates are package-private fields of the packet's base class. */
    public static TileEntity buttonDevice(Object packet, World world) {
        try {
            return world.getTileEntity(
                coordinate(packet, "xCoord"),
                coordinate(packet, "yCoord"),
                coordinate(packet, "zCoord"));
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    private static int coordinate(Object packet, String name) throws ReflectiveOperationException {
        for (Class<?> type = packet.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.getInt(packet);
            } catch (NoSuchFieldException next) {
                // Keep looking in the superclass.
            }
        }
        throw new NoSuchFieldException(name);
    }

    public static FluidStack[] contents(TileEntity tile) {
        if (!(tile instanceof IFluidHandler)) return new FluidStack[0];
        FluidTankInfo[] tanks = ((IFluidHandler) tile).getTankInfo(ForgeDirection.UNKNOWN);
        if (tanks == null) return new FluidStack[0];
        FluidStack[] contents = new FluidStack[tanks.length];
        for (int i = 0; i < tanks.length; i++)
            contents[i] = tanks[i] == null || tanks[i].fluid == null ? null : tanks[i].fluid.copy();
        return contents;
    }

    private static Boolean sinkRequiresWater;

    /** Cooking for Blockheads' sinkRequiresWater option; read once, as it needs a restart. */
    public static boolean sinkRequiresWater() {
        if (sinkRequiresWater == null) {
            try {
                sinkRequiresWater = Class.forName("net.blay09.mods.cookingforblockheads.CookingConfig")
                    .getField("sinkRequiresWater")
                    .getBoolean(null);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                sinkRequiresWater = true;
            }
        }
        return sinkRequiresWater;
    }

    public static int total(FluidStack[] contents) {
        int total = 0;
        for (FluidStack fluid : contents) if (fluid != null) total += fluid.amount;
        return total;
    }

    public static boolean same(FluidStack[] a, FluidStack[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == null ? b[i] != null : !a[i].isFluidStackIdentical(b[i])) return false;
        }
        return true;
    }
}
