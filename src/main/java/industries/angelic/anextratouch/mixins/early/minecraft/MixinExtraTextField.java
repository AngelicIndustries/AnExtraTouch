package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.TypingSounds;

@Mixin(GuiTextField.class)
public abstract class MixinExtraTextField {

    @WrapMethod(method = "textboxKeyTyped")
    private boolean anextratouch$typing(char c, int key, Operation<Boolean> original) {
        GuiTextField self = (GuiTextField) (Object) this;
        String text = Config.extraTyping ? self.getText() : null;
        int cursor = self.getCursorPosition(), selection = self.getSelectionEnd();
        boolean control = GuiScreen.isCtrlKeyDown();
        // Other mods can handle shortcuts in a cancelling HEAD injection.
        boolean handled = original.call(c, key);
        TypingSounds.changed(
            text,
            self.getText(),
            cursor,
            self.getCursorPosition(),
            selection,
            self.getSelectionEnd(),
            c,
            key,
            control);
        return handled;
    }
}
