package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSoundResources;

@Mixin(SoundHandler.class)
public abstract class MixinExtraSoundResources {

    @Shadow
    private SoundRegistry sndRegistry;

    @Inject(method = "loadSoundResource", at = @At("HEAD"), cancellable = true)
    private void anextratouch$aliases(ResourceLocation event, SoundList list, CallbackInfo ci) {
        if (ExtraSoundResources.load((SoundHandler) (Object) this, sndRegistry, event, list)) ci.cancel();
    }
}
