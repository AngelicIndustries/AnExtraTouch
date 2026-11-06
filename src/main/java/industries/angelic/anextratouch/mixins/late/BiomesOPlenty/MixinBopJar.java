package industries.angelic.anextratouch.mixins.late.BiomesOPlenty;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Scooping honey into an empty jar. */
@Pseudo
@Mixin(targets = "biomesoplenty.common.items.ItemJarEmpty", remap = false)
public abstract class MixinBopJar {

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$fill(ItemStack stack, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        int jars = stack.stackSize;
        ItemStack result = original.call(stack, world, player);
        // The last jar is replaced by the filled one; otherwise one is used up and the filled jar goes elsewhere.
        if (!world.isRemote && (result != stack || stack.stackSize < jars)) InteractionCues.send(
            world,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ),
            InteractionCues.Cue.BOTTLE_FILL);
        return result;
    }
}
