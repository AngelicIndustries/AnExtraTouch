package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.entity.EntityPlayerSP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.camera.DecoupledCameraHandler;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Inject(method = "updateEntityActionState", at = @At("RETURN"))
    private void anextratouch$afterUpdateEntityActionState(CallbackInfo ci) {
        DecoupledCameraHandler.transformMovement((EntityPlayerSP) (Object) this);
    }
}
