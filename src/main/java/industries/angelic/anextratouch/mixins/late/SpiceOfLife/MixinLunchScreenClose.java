package industries.angelic.anextratouch.mixins.late.SpiceOfLife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.LunchLids;

/** Closing a lunch box or bag's screen closes its lid, in silence. */
@Pseudo
@Mixin(targets = "squeek.spiceoflife.inventory.ContainerFoodContainer", remap = false)
public abstract class MixinLunchScreenClose {

    @Shadow
    public abstract ItemStack getItemStack();

    @WrapMethod(method = { "onContainerClosed", "func_75134_a" })
    private void anextratouch$closed(EntityPlayer player, Operation<Void> original) {
        ItemStack stack = getItemStack();
        boolean open = LunchLids.open(stack);
        original.call(player);
        if (stack != null) LunchLids.changed(player, stack, open);
    }
}
