package industries.angelic.anextratouch.mixins.late.Growthcraft;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

@Pseudo
@Mixin(
    targets = { "growthcraft.apples.common.block.BlockApple", "growthcraft.hops.common.block.BlockHops",
        "growthcraft.grapes.common.block.BlockGrapeBlock", "growthcraft.milk.common.block.BlockHangingCurds" },
    remap = false)
public abstract class MixinGrowthcraftHarvest {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$harvest(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        // Ripe apples, grapes and dried curds drop as items; hops lose their fruit.
        if (!world.isRemote
            && InteractionCues.changed(block, meta, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
            InteractionCues.send(world, x, y, z, InteractionCues.Cue.HARVEST);
        return handled;
    }
}
