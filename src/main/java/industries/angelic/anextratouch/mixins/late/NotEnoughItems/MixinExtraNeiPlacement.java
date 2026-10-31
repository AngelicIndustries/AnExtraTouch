package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import codechicken.nei.recipe.CheatItemHandler;
import industries.angelic.anextratouch.handlers.client.extrasounds.NeiInventorySounds;
import industries.angelic.anextratouch.handlers.client.extrasounds.NeiInventorySounds.Change;

@Mixin(value = CheatItemHandler.class, remap = false)
public abstract class MixinExtraNeiPlacement {

    @WrapOperation(
        method = "handleDragNDrop",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/nei/NEIClientUtils;setSlotContents(ILnet/minecraft/item/ItemStack;Z)V"))
    private void anextratouch$place(int slot, ItemStack stack, boolean container, Operation<Void> original) {
        // This call happens only after permission, capacity and slot-validity checks.
        Change change = NeiInventorySounds
            .placement(Minecraft.getMinecraft().thePlayer.openContainer.getSlot(slot), stack);
        original.call(slot, stack, container);
        NeiInventorySounds.submit(change);
    }
}
