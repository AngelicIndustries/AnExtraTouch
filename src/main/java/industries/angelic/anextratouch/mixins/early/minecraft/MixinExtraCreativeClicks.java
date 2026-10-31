package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.extrasounds.InventorySounds;

@Mixin(GuiContainerCreative.class)
public abstract class MixinExtraCreativeClicks {

    @Shadow
    private static InventoryBasic field_147060_v;
    @Shadow
    private Slot field_147064_C;
    @Unique
    private InventorySounds anextratouch$click;

    @Inject(method = "handleMouseClick", at = @At("HEAD"))
    private void anextratouch$before(Slot slot, int index, int button, int mode, CallbackInfo ci) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        anextratouch$click = InventorySounds.begin(
            player,
            ((GuiContainerCreative) (Object) this).inventorySlots,
            slot,
            index,
            button,
            mode,
            true,
            slot != null && slot.inventory == field_147060_v,
            slot != null && slot == field_147064_C);
    }

    @Inject(method = "handleMouseClick", at = @At("RETURN"))
    private void anextratouch$after(Slot slot, int index, int button, int mode, CallbackInfo ci) {
        InventorySounds click = anextratouch$click;
        anextratouch$click = null;
        if (click != null) click.finish();
    }
}
