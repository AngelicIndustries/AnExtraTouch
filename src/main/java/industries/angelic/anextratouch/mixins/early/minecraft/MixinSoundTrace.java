package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.anextratouch.handlers.client.extrasounds.SoundInspector;

/** Reports the entry the sound manager actually picked to play, for {@code /aet_sound trace}. */
@Mixin(SoundManager.class)
public abstract class MixinSoundTrace {

    @ModifyExpressionValue(
        method = "playSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/audio/SoundEventAccessorComposite;func_148720_g()Lnet/minecraft/client/audio/SoundPoolEntry;"))
    private SoundPoolEntry anextratouch$trace(SoundPoolEntry entry, @Local(argsOnly = true) ISound sound) {
        SoundInspector.played(sound, entry);
        return entry;
    }
}
