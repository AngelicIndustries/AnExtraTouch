package industries.angelic.anextratouch.mixins.late.SpiceOfLife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * Sneak-using a lunch box or bag flips its lid, stored as an {@code Open} NBT flag. Opening its screen also sets the
 * flag, but that already plays the screen sound.
 */
@Pseudo
@Mixin(targets = "squeek.spiceoflife.items.ItemFoodContainer", remap = false)
public abstract class MixinLunchLid {

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$lid(ItemStack stack, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        boolean open = open(stack);
        ItemStack result = original.call(stack, world, player);
        if (!world.isRemote && player.isSneaking() && open != open(stack)) {
            String name = Item.itemRegistry.getNameForObject(stack.getItem());
            boolean box = name != null && name.endsWith("lunchbox");
            InteractionCues.Cue cue = box
                ? open ? InteractionCues.Cue.LUNCHBOX_CLOSE : InteractionCues.Cue.LUNCHBOX_OPEN
                : open ? InteractionCues.Cue.LUNCHBAG_CLOSE : InteractionCues.Cue.LUNCHBAG_OPEN;
            InteractionCues.send(
                world,
                MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ),
                cue);
        }
        return result;
    }

    private static boolean open(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .getBoolean("Open");
    }
}
