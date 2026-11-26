package industries.angelic.anextratouch.mixins.late.battlegear2;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * Right-clicking a Battlegear quiver puts it in its Baubles slot and sneak-right-clicking spills its arrows, both in
 * silence. Equipping through the Baubles screen already clicks like any slot, so only the right-click equip is cued.
 */
@Pseudo
@Mixin(targets = "mods.battlegear2.items.ItemQuiver", remap = false)
public abstract class MixinQuiver {

    @Unique
    private static final ThreadLocal<Boolean> anextratouch$rightClick = new ThreadLocal<>();

    @Shadow
    public abstract int getSlotCount(ItemStack quiver);

    @Shadow
    public abstract ItemStack getStackInSlot(ItemStack quiver, int slot);

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$use(ItemStack quiver, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        int arrows = anextratouch$arrows(quiver);
        anextratouch$rightClick.set(Boolean.TRUE);
        ItemStack result;
        try {
            result = original.call(quiver, world, player);
        } finally {
            anextratouch$rightClick.remove();
        }
        if (!world.isRemote && player.isSneaking() && anextratouch$arrows(quiver) < arrows)
            anextratouch$cue(player, InteractionCues.Cue.QUIVER_DUMP);
        return result;
    }

    @Inject(method = "onEquipped", at = @At("HEAD"))
    private void anextratouch$equipped(ItemStack quiver, EntityLivingBase wearer, CallbackInfo ci) {
        if (anextratouch$rightClick.get() != null && !wearer.worldObj.isRemote)
            anextratouch$cue(wearer, InteractionCues.Cue.QUIVER_EQUIP);
    }

    @Unique
    private int anextratouch$arrows(ItemStack quiver) {
        int arrows = 0;
        for (int slot = 0; slot < getSlotCount(quiver); slot++) {
            ItemStack stack = getStackInSlot(quiver, slot);
            if (stack != null) arrows += stack.stackSize;
        }
        return arrows;
    }

    @Unique
    private static void anextratouch$cue(EntityLivingBase entity, InteractionCues.Cue cue) {
        InteractionCues.send(
            entity.worldObj,
            MathHelper.floor_double(entity.posX),
            MathHelper.floor_double(entity.posY),
            MathHelper.floor_double(entity.posZ),
            cue);
    }
}
