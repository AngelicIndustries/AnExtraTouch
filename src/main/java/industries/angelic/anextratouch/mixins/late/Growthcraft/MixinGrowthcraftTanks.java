package industries.angelic.anextratouch.mixins.late.Growthcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Barrels, kegs, culture jars and other Growthcraft tanks. Their fluid handling runs on the server only. */
@Pseudo
@Mixin(targets = "growthcraft.core.common.block.GrcBlockContainer", remap = false)
public abstract class MixinGrowthcraftTanks {

    @WrapMethod(method = "playerFillTank")
    private boolean anextratouch$poured(World world, int x, int y, int z, IFluidHandler tank, ItemStack stack,
        EntityPlayer player, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(stack);
        boolean poured = original.call(world, x, y, z, tank, stack, player);
        if (poured) InteractionCues.send(world, x, y, z, InteractionCues.container(held, false));
        return poured;
    }

    @WrapMethod(method = "playerDrainTank")
    private boolean anextratouch$drawn(World world, int x, int y, int z, IFluidHandler tank, ItemStack stack,
        EntityPlayer player, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(stack);
        boolean drawn = original.call(world, x, y, z, tank, stack, player);
        if (drawn) InteractionCues.send(world, x, y, z, InteractionCues.container(held, true));
        return drawn;
    }
}
