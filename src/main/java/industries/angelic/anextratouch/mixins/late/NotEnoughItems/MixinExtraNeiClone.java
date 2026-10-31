package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.PanelWidget;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;
import industries.angelic.anextratouch.handlers.client.extrasounds.InventorySoundRegistry;

@Mixin(value = PanelWidget.class, remap = false)
public abstract class MixinExtraNeiClone {

    @Shadow
    public ItemStack draggedStack;

    @WrapMethod(method = "handleClick")
    private boolean anextratouch$clone(int x, int y, int button, Operation<Boolean> original) {
        ItemStack before = draggedStack;
        boolean handled = original.call(x, y, button);
        if (button == 2 && before == null && draggedStack != null && Config.extraCreativeActions)
            ExtraSounds.ui("item.clone");
        return handled;
    }

    @WrapMethod(method = "mouseDragged")
    private void anextratouch$pickup(int x, int y, int button, long time, Operation<Void> original) {
        ItemStack before = draggedStack;
        original.call(x, y, button, time);
        if (before == null && draggedStack != null && Config.extraInventoryClicks)
            ExtraSounds.ui(InventorySoundRegistry.sound(draggedStack), 1.0F);
    }
}
