package industries.angelic.anextratouch.mixins.late.BiomesOPlenty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Pouring a BOP bucket into a tank or the world, which unlike vanilla buckets made no sound. */
@Pseudo
@Mixin(targets = "biomesoplenty.common.items.ItemBOPBucket", remap = false)
public abstract class MixinBopBucket {

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$pour(ItemStack stack, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        ItemStack result = original.call(stack, world, player);
        // Creative mode keeps the full bucket, which is indistinguishable from a miss and stays silent.
        if (!world.isRemote && result != stack && result != null && result.getItem() == Items.bucket)
            InteractionCues.send(
                world,
                MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY),
                MathHelper.floor_double(player.posZ),
                InteractionCues.Cue.BUCKET_EMPTY);
        return result;
    }
}
