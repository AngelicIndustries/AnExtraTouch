package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;

@Mixin(EntityClientPlayerMP.class)
public abstract class MixinExtraItemDrop {

    @Inject(method = "dropOneItem", at = @At("RETURN"))
    private void anextratouch$drop(boolean all, CallbackInfoReturnable<EntityItem> cir) {
        EntityClientPlayerMP self = (EntityClientPlayerMP) (Object) this;
        if (self != Minecraft.getMinecraft().thePlayer) return;
        ItemStack stack = self.getHeldItem();
        if (stack != null) ExtraSounds.drop(stack, all ? stack.stackSize : 1);
    }
}
