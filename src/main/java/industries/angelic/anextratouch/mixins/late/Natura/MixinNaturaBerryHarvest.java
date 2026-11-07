package industries.angelic.anextratouch.mixins.late.Natura;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Natura's ripe berry bushes, overworld and Nether, hand over their berries on either click, in silence. */
@Pseudo
@Mixin(targets = { "mods.natura.blocks.crops.BerryBush", "mods.natura.blocks.crops.NetherBerryBush" }, remap = false)
public abstract class MixinNaturaBerryHarvest {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$pickByUse(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        anextratouch$picked(world, x, y, z, block, meta);
        return handled;
    }

    @WrapMethod(method = { "onBlockClicked", "func_149699_a" })
    private void anextratouch$pickByHit(World world, int x, int y, int z, EntityPlayer player,
        Operation<Void> original) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        original.call(world, x, y, z, player);
        anextratouch$picked(world, x, y, z, block, meta);
    }

    private static void anextratouch$picked(World world, int x, int y, int z, Block block, int meta) {
        // Picking drops the bush back from ripe to grown metadata.
        if (!world.isRemote
            && InteractionCues.changed(block, meta, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
            InteractionCues.send(world, x, y, z, InteractionCues.Cue.HARVEST);
    }
}
