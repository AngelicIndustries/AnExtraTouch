package industries.angelic.anextratouch.mixins.late.saltymod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

@Pseudo
@Mixin(targets = "darkbum.saltymod.block.BlockEvaporator", remap = false)
public abstract class MixinEvaporatorTank {

    // Pouring a held bucket or bottle into the evaporator. Its activation runs on the server only.
    @WrapMethod(method = "fillTank")
    private static boolean anextratouch$poured(World world, int x, int y, int z, IFluidHandler tank, ItemStack stack,
        EntityPlayer player, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(stack);
        boolean poured = original.call(world, x, y, z, tank, stack, player);
        if (poured) InteractionCues.send(world, x, y, z, InteractionCues.container(held, false));
        return poured;
    }

    @WrapMethod(method = "drainTank")
    private boolean anextratouch$drawn(World world, int x, int y, int z, IFluidHandler tank, ItemStack stack,
        EntityPlayer player, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(stack);
        boolean drawn = original.call(world, x, y, z, tank, stack, player);
        if (drawn) InteractionCues.send(world, x, y, z, InteractionCues.container(held, true));
        return drawn;
    }
}
