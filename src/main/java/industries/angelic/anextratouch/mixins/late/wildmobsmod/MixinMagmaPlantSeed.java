package industries.angelic.anextratouch.mixins.late.wildmobsmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Planting a magma plant seed spawns the plant as a creature, in silence. */
@Pseudo
@Mixin(targets = "com.wildmobsmod.items.ItemMagmaPlantSeed", remap = false)
public abstract class MixinMagmaPlantSeed {

    @WrapMethod(method = { "onItemUse", "func_77648_a" })
    private boolean anextratouch$use(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, Operation<Boolean> original) {
        int count = stack.stackSize;
        boolean used = original.call(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        // Each use spends one item, except in creative.
        if (!world.isRemote && used && (stack.stackSize < count || player.capabilities.isCreativeMode))
            InteractionCues.sendOnFace(world, x, y, z, side, InteractionCues.Cue.PLANT);
        return used;
    }
}
