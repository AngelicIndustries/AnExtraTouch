package industries.angelic.anextratouch.mixins.late.TwilightForest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** The timewood, transformation, miner's and sorting cores switch on and off on use, in silence. */
@Pseudo
@Mixin(targets = "twilightforest.block.BlockTFMagicLogSpecial", remap = false)
public abstract class MixinMagicLogCore {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$toggle(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        int now = world.getBlockMetadata(x, y, z);
        // A soft mallet mutes the time core without switching it; that keeps the same metadata.
        if (!world.isRemote && now != meta) InteractionCues.send(world, x, y, z, InteractionCues.core(now));
        return handled;
    }
}
