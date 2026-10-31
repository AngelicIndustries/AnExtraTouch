package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundHandler.class)
public interface AccessorExtraSoundHandler {

    @Accessor("sndRegistry")
    SoundRegistry anextratouch$soundRegistry();
}
