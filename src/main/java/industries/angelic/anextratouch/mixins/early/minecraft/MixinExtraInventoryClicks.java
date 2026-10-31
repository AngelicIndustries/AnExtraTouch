package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.handlers.client.extrasounds.InventorySounds;

@Mixin(PlayerControllerMP.class)
public abstract class MixinExtraInventoryClicks {

    @Unique
    private InventorySounds anextratouch$click;

    @Inject(method = "windowClick", at = @At("HEAD"))
    private void anextratouch$before(int window, int index, int button, int mode, EntityPlayer player,
        CallbackInfoReturnable<ItemStack> cir) {
        Slot slot = index >= 0 && index < player.openContainer.inventorySlots.size()
            ? player.openContainer.getSlot(index)
            : null;
        anextratouch$click = InventorySounds
            .begin(player, player.openContainer, slot, index, button, mode, false, false, false);
    }

    @Inject(method = "windowClick", at = @At("RETURN"))
    private void anextratouch$after(int window, int index, int button, int mode, EntityPlayer player,
        CallbackInfoReturnable<ItemStack> cir) {
        InventorySounds click = anextratouch$click;
        anextratouch$click = null;
        if (click != null) click.finish();
    }
}
