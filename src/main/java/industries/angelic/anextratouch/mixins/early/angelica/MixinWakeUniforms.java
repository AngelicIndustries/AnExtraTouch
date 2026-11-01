package industries.angelic.anextratouch.mixins.early.angelica;

import net.coderbot.iris.gl.program.ProgramUniforms;
import net.coderbot.iris.gl.uniform.UniformHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.effects.AngelicaWakeBindings;

@Mixin(value = ProgramUniforms.Builder.class, remap = false)
public abstract class MixinWakeUniforms {

    @Inject(method = "buildUniforms", at = @At("HEAD"))
    private void anextratouch$wakeUniforms(CallbackInfoReturnable<ProgramUniforms> cir) {
        if (Config.experimentalWaterWakes) AngelicaWakeBindings.uniforms((UniformHolder) this);
    }
}
