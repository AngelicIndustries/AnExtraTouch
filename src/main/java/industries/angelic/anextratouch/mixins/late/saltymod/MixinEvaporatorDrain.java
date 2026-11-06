package industries.angelic.anextratouch.mixins.late.saltymod;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.anextratouch.compat.InteractionCues;

/** The evaporator's drain button empties its tank on the server. An empty tank stays quiet. */
@Pseudo
@Mixin(targets = "darkbum.saltymod.network.EvaporatorButtonMessage$Handler", remap = false)
public abstract class MixinEvaporatorDrain {

    @WrapOperation(
        method = "onMessage(Ldarkbum/saltymod/network/EvaporatorButtonMessage;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fluids/FluidTank;setFluid(Lnet/minecraftforge/fluids/FluidStack;)V",
            remap = false))
    private void anextratouch$drain(FluidTank tank, FluidStack fluid, Operation<Void> original,
        @Local TileEntity evaporator) {
        int before = tank.getFluidAmount();
        original.call(tank, fluid);
        if (before > 0 && evaporator != null) InteractionCues.send(
            evaporator.getWorldObj(),
            evaporator.xCoord,
            evaporator.yCoord,
            evaporator.zCoord,
            InteractionCues.Cue.DRAIN);
    }
}
