package industries.angelic.anextratouch.mixins.late.SpiceOfLife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.LunchLids;

/** Using a lunch box or bag opens its screen and lid, and sneak-using it flips the lid, in silence. */
@Pseudo
@Mixin(targets = "squeek.spiceoflife.items.ItemFoodContainer", remap = false)
public abstract class MixinLunchLid {

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$lid(ItemStack stack, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        boolean open = LunchLids.open(stack);
        ItemStack result = original.call(stack, world, player);
        LunchLids.changed(player, stack, open);
        return result;
    }
}
