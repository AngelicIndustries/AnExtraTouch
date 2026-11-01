package industries.angelic.anextratouch.compat.etfuturum;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.api.MultiBlockSoundRegistry;
import ganymedes01.etfuturum.api.mappings.MultiBlockSoundContainer;
import industries.angelic.anextratouch.mixins.late.etfuturum.AccessorBasicMultiBlockSound;

public final class EfrBlockSounds {

    private EfrBlockSounds() {}

    public static Block.SoundType sound(Block block, int metadata) {
        MultiBlockSoundContainer mapping = MultiBlockSoundRegistry.multiBlockSounds.get(block);
        // World-dependent handlers cannot be evaluated for an inventory stack. Basic mappings can.
        if (mapping instanceof AccessorBasicMultiBlockSound) {
            Block.SoundType sound = ((AccessorBasicMultiBlockSound) mapping).anextratouch$types()
                .get(metadata);
            if (sound != null) return sound;
        }
        return block.stepSound;
    }
}
