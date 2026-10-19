package industries.angelic.anextratouch.mixins.early.angelica;

import net.coderbot.iris.pipeline.DeferredWorldRenderingPipeline;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.effects.AngelicaShaderHelper;

@Mixin(value = DeferredWorldRenderingPipeline.class, remap = false)
public abstract class MixinDeferredWorldRenderingPipeline {

    @Inject(method = "beginTranslucents", at = @At("HEAD"))
    private void anextratouch$captureSurfaceDepthAfterHands(CallbackInfo ci) {
        // Angelica draws solid hands inside sortAndRender(pass=1), before this boundary.
        // Capture the same scene as depthtex1: opaque world + hands, without translucent water.
        AngelicaShaderHelper.captureSurfaceDepth();
    }
}
