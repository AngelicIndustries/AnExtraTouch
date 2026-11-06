package industries.angelic.anextratouch.mixins.late.saltymod;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Salt grass tills itself when a hoe is used on it, so the hoe's own till sound never plays. */
@Pseudo
@Mixin(targets = "darkbum.saltymod.block.BlockSaltGrass", remap = false)
public abstract class MixinSaltGrassTill {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$till(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        Block before = world.getBlock(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        if (!world.isRemote && before != Blocks.farmland && world.getBlock(x, y, z) == Blocks.farmland)
            InteractionCues.send(world, x, y, z, InteractionCues.Cue.TILL);
        return handled;
    }
}
