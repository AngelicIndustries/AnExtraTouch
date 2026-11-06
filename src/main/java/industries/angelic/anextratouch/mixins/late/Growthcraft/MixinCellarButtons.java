package industries.angelic.anextratouch.mixins.late.Growthcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.FluidTanks;
import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * The cellar devices' discard buttons and the brew kettle's switch button act on the server. Switching clears both
 * tanks internally, so the packets are hooked rather than the tanks.
 */
@Pseudo
@Mixin(
    targets = { "growthcraft.cellar.network.PacketClearTankButton",
        "growthcraft.cellar.network.PacketClearTankButtonWByte", "growthcraft.cellar.network.PacketSwitchTankButton" },
    remap = false)
public abstract class MixinCellarButtons {

    @WrapMethod(method = "handleServerSide")
    private void anextratouch$tanks(EntityPlayer player, Operation<Void> original) {
        TileEntity device = FluidTanks.buttonDevice(this, player.worldObj);
        FluidStack[] before = FluidTanks.contents(device);
        original.call(player);
        if (device == null) return;
        FluidStack[] after = FluidTanks.contents(device);
        boolean switching = getClass().getSimpleName()
            .contains("Switch");
        InteractionCues.Cue cue = switching ? FluidTanks.same(before, after) ? null : InteractionCues.Cue.TANK_SWITCH
            : FluidTanks.total(after) < FluidTanks.total(before) ? InteractionCues.Cue.DRAIN : null;
        InteractionCues.send(device.getWorldObj(), device.xCoord, device.yCoord, device.zCoord, cue);
    }
}
