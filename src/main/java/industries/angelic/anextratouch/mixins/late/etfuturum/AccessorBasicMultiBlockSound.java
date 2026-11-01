package industries.angelic.anextratouch.mixins.late.etfuturum;

import java.util.Map;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import ganymedes01.etfuturum.api.mappings.BasicMultiBlockSound;

@Mixin(value = BasicMultiBlockSound.class, remap = false)
public interface AccessorBasicMultiBlockSound {

    @Accessor("types")
    Map<Integer, Block.SoundType> anextratouch$types();
}
