package industries.angelic.anextratouch.mixins.early.minecraft;

import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.extrasounds.UiSoundSources;
import paulscode.sound.Channel;
import paulscode.sound.Source;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;
import paulscode.sound.libraries.SourceLWJGLOpenAL;

@Mixin(value = SourceLWJGLOpenAL.class, remap = false)
public class MixinExtraUiSoundSource {

    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lpaulscode/sound/Channel;play()V"))
    private void anextratouch$centerUiSound(Channel channel, CallbackInfo ci) {
        Source source = (Source) (Object) this;
        if (!(source.channel instanceof ChannelLWJGLOpenAL openAL)) return;
        if (openAL.attachedSource != source || openAL.ALSource == null) return;
        // No distance attenuation still allows panning. UI coordinates must be relative
        // to the listener, and pooled channels must return to world space on reuse.
        AL10.alSourcei(
            openAL.ALSource.get(0),
            AL10.AL_SOURCE_RELATIVE,
            UiSoundSources.isRelative(source.sourcename) ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    @Inject(method = "cleanup", at = @At("HEAD"))
    private void anextratouch$forgetUiSound(CallbackInfo ci) {
        UiSoundSources.remove(((Source) (Object) this).sourcename);
    }
}
