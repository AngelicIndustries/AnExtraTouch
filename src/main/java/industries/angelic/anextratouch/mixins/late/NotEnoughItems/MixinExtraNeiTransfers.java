package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.NEIController;
import industries.angelic.anextratouch.handlers.client.extrasounds.InventorySounds;

@Mixin(value = NEIController.class, remap = false)
public abstract class MixinExtraNeiTransfers {

    @WrapMethod(method = "handleSlotClick")
    private boolean anextratouch$click(GuiContainer gui, int index, int button, Slot slot, int modifier,
        boolean consumed, Operation<Boolean> original) {
        InventorySounds.beginBatch(Minecraft.getMinecraft().thePlayer);
        try {
            return original.call(gui, index, button, slot, modifier, consumed);
        } finally {
            InventorySounds.endBatch();
        }
    }

    @WrapMethod(method = "mouseScrolled")
    private boolean anextratouch$scroll(GuiContainer gui, int x, int y, int direction, Operation<Boolean> original) {
        InventorySounds.beginBatch(Minecraft.getMinecraft().thePlayer);
        try {
            return original.call(gui, x, y, direction);
        } finally {
            InventorySounds.endBatch();
        }
    }

    @WrapMethod(method = "lastKeyTyped")
    private boolean anextratouch$key(GuiContainer gui, char key, int code, Operation<Boolean> original) {
        InventorySounds.beginBatch(Minecraft.getMinecraft().thePlayer);
        try {
            return original.call(gui, key, code);
        } finally {
            InventorySounds.endBatch();
        }
    }
}
