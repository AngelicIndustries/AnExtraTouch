package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.NEIClientUtils;
import industries.angelic.anextratouch.handlers.client.extrasounds.NeiInventorySounds;
import industries.angelic.anextratouch.handlers.client.extrasounds.NeiInventorySounds.Change;

@Mixin(value = NEIClientUtils.class, remap = false)
public abstract class MixinExtraNeiItems {

    @WrapMethod(method = "giveStack(Lnet/minecraft/item/ItemStack;IZ)V")
    private static void anextratouch$give(ItemStack stack, int count, boolean infinite, Operation<Void> original) {
        Change change = NeiInventorySounds.pickup(stack);
        original.call(stack, count, infinite);
        NeiInventorySounds.submit(change);
    }

    @WrapMethod(method = { "deleteSlotStack", "decreaseSlotStack" })
    private static void anextratouch$deleteSlot(int slot, Operation<Void> original) {
        Change change = NeiInventorySounds.delete(slot, null, false);
        boolean completed = false;
        try {
            original.call(slot);
            completed = true;
        } finally {
            NeiInventorySounds.endDelete(change, completed);
        }
    }

    @WrapMethod(method = "deleteItemsOfType")
    private static void anextratouch$deleteType(ItemStack stack, Operation<Void> original) {
        Change change = NeiInventorySounds.delete(-1, stack, false);
        boolean completed = false;
        try {
            original.call(stack);
            completed = true;
        } finally {
            NeiInventorySounds.endDelete(change, completed);
        }
    }

    @WrapMethod(method = "deleteEverything")
    private static void anextratouch$deleteAll(Operation<Void> original) {
        Change change = NeiInventorySounds.delete(-1, null, true);
        boolean completed = false;
        try {
            original.call();
            completed = true;
        } finally {
            NeiInventorySounds.endDelete(change, completed);
        }
    }
}
