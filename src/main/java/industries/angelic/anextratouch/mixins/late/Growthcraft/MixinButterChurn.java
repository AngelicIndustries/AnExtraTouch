package industries.angelic.anextratouch.mixins.late.Growthcraft;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.compat.InteractionCues;

@Pseudo
@Mixin(targets = "growthcraft.milk.common.tileentity.TileEntityButterChurn", remap = false)
public abstract class MixinButterChurn {

    // The work state is Growthcraft's own enum: CHURN for a stroke, PRODUCE for the one that finishes.
    @Inject(method = "doWork", at = @At("RETURN"))
    private void anextratouch$churn(CallbackInfoReturnable<Enum<?>> cir) {
        Enum<?> state = cir.getReturnValue();
        if (state == null) return;
        InteractionCues.Cue cue = "PRODUCE".equals(state.name()) ? InteractionCues.Cue.CHURN_DONE
            : "CHURN".equals(state.name()) ? InteractionCues.Cue.CHURN : null;
        TileEntity tile = (TileEntity) (Object) this;
        InteractionCues.send(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, cue);
    }
}
