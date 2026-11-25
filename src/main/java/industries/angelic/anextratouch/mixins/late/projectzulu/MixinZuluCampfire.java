package industries.angelic.anextratouch.mixins.late.projectzulu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Project Zulu's campfire is lit with a torch or flint and steel and put out with a water bucket, in silence. */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.blocks.BlockCampfire", remap = false)
public abstract class MixinZuluCampfire {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$use(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        // Lighting adds 2 to the metadata, putting it out takes 2 away.
        int now = world.getBlockMetadata(x, y, z);
        if (!world.isRemote && now == meta + 2)
            InteractionCues.send(world, x, y, z, InteractionCues.Cue.CAMPFIRE_LIGHT);
        else if (!world.isRemote && now == meta - 2)
            InteractionCues.send(world, x, y, z, InteractionCues.Cue.CAMPFIRE_OUT);
        return handled;
    }
}
