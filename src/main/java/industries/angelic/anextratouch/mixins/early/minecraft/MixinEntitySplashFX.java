package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.effects.NeutralParticleTexture;
import industries.angelic.anextratouch.handlers.client.effects.WaterParticleTint;

@Mixin(value = EntitySplashFX.class, priority = 500)
public class MixinEntitySplashFX {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void anextratouch$tintSplash(World world, double x, double y, double z, double motionX, double motionY,
        double motionZ, CallbackInfo ci) {
        NeutralParticleTexture.ensureApplied();
        EntityFX particle = (EntityFX) (Object) this;
        WaterParticleTint.applyTint(particle, true);
    }
}
