package industries.angelic.anextratouch.mixins.early.angelica;

import java.util.Optional;

import net.coderbot.iris.shaderpack.ProgramSource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.effects.water.WakeShaderAdapter;

@Mixin(value = ProgramSource.class, remap = false)
public abstract class MixinWakeProgramSource {

    @Shadow
    @Final
    private String name;
    @Unique
    private Optional<String> anextratouch$wakeSource;

    @Inject(method = "getFragmentSource", at = @At("RETURN"), cancellable = true)
    private synchronized void anextratouch$waterNormals(CallbackInfoReturnable<Optional<String>> cir) {
        if (!Config.experimentalWaterWakes || !"gbuffers_water".equals(name)) return;
        if (anextratouch$wakeSource == null) {
            Optional<String> original = cir.getReturnValue();
            if (!original.isPresent()) return;
            String patched = WakeShaderAdapter.patch(name, original.get());
            anextratouch$wakeSource = Optional.of(patched);
            if (patched.equals(original.get())) {
                AnExtraTouch.LOG.warn("Experimental water wakes: unrecognized water shader; retaining regular wakes");
            } else {
                AnExtraTouch.LOG.info("Experimental water wakes: installed water normal adapter");
            }
        }
        cir.setReturnValue(anextratouch$wakeSource);
    }
}
