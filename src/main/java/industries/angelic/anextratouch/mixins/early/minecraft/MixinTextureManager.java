package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.effects.CoastalWaveShaderMesh;
import industries.angelic.anextratouch.handlers.client.effects.NeutralParticleTexture;
import industries.angelic.anextratouch.handlers.client.effects.SplashShaderMesh;

@Mixin(TextureManager.class)
public class MixinTextureManager {

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void anextratouch$reloadNeutralWaterParticles(IResourceManager resourceManager, CallbackInfo ci) {
        NeutralParticleTexture.invalidate();
        SplashShaderMesh.invalidate();
        CoastalWaveShaderMesh.invalidate();
    }
}
