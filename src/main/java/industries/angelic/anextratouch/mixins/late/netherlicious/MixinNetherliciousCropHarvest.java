package industries.angelic.anextratouch.mixins.late.netherlicious;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * Netherlicious harvests and replants ripe abyssal oat and devilish maize on use, in silence. Hunger Overhaul usually
 * harvests them first, which plays the same cue; then these find a seedling and stay quiet.
 */
@Pseudo
@Mixin(
    targets = { "DelirusCrux.Netherlicious.Common.Blocks.Crops.CropAbyssalOat",
        "DelirusCrux.Netherlicious.Common.Blocks.Crops.CropDevilishMaize" },
    remap = false)
public abstract class MixinNetherliciousCropHarvest {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$harvest(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        if (!world.isRemote
            && InteractionCues.changed(block, meta, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
            InteractionCues.send(world, x, y, z, InteractionCues.crop(Block.blockRegistry.getNameForObject(block)));
        return handled;
    }
}
