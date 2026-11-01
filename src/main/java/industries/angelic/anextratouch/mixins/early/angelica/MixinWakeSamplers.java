package industries.angelic.anextratouch.mixins.early.angelica;

import net.coderbot.iris.gl.program.ProgramSamplers;
import net.coderbot.iris.gl.sampler.SamplerHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.handlers.client.effects.AngelicaWakeBindings;

@Mixin(value = ProgramSamplers.Builder.class, remap = false)
public abstract class MixinWakeSamplers {

    @Inject(method = "build", at = @At("HEAD"))
    private void anextratouch$bindWakeField(CallbackInfoReturnable<ProgramSamplers> cir) {
        AngelicaWakeBindings.samplers((SamplerHolder) this);
    }
}
