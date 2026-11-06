package industries.angelic.anextratouch.mixins.late.Growthcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

@Pseudo
@Mixin(targets = "growthcraft.core.common.block.BlockPaddyBase", remap = false)
public abstract class MixinGrowthcraftPaddy {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$flood(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(player.inventory.getCurrentItem());
        int meta = world.getBlockMetadata(x, y, z);
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        // A water container floods the paddies around it. Creative mode keeps the container.
        boolean flooded = meta != world.getBlockMetadata(x, y, z)
            || held != null && !ItemStack.areItemStacksEqual(held, player.inventory.getCurrentItem());
        if (!world.isRemote && held != null && flooded)
            InteractionCues.send(world, x, y, z, InteractionCues.container(held, false));
        return handled;
    }
}
