package industries.angelic.anextratouch.mixins.late.wildmobsmod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Emptying a jellyfish or sea scorpion bucket lets the creature into the water, in silence. */
@Pseudo
@Mixin(
    targets = { "com.wildmobsmod.items.ItemJellyfishBucket", "com.wildmobsmod.items.ItemSeaScorpionBucket" },
    remap = false)
public abstract class MixinCreatureBucketRelease {

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$release(ItemStack stack, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        ItemStack result = original.call(stack, world, player);
        // A release hands back the plain water or lava bucket.
        if (!world.isRemote && result != null && result.getItem() != stack.getItem()) InteractionCues.send(
            world,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ),
            InteractionCues.Cue.BUCKET_RELEASE);
        return result;
    }
}
