package industries.angelic.anextratouch.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundEventAccessorComposite.class)
public interface AccessorSoundEventComposite {

    @Accessor("soundPool")
    List<ISoundEventAccessor> anextratouch$soundPool();

    @Accessor("eventVolume")
    double anextratouch$eventVolume();

    @Accessor("eventPitch")
    double anextratouch$eventPitch();
}
