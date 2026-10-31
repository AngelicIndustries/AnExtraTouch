package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.TypingSounds;

@Mixin(GuiEditSign.class)
public abstract class MixinExtraSignTyping {

    @Shadow
    private TileEntitySign tileSign;
    @Shadow
    private int editLine;
    @Unique
    private String anextratouch$text;
    @Unique
    private int anextratouch$line;

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void anextratouch$before(char c, int key, CallbackInfo ci) {
        anextratouch$text = Config.extraTyping ? String.join("\n", tileSign.signText) : null;
        anextratouch$line = editLine;
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void anextratouch$after(char c, int key, CallbackInfo ci) {
        TypingSounds.changed(
            anextratouch$text,
            String.join("\n", tileSign.signText),
            anextratouch$line,
            editLine,
            anextratouch$line,
            editLine,
            c,
            key,
            GuiScreen.isCtrlKeyDown());
        anextratouch$text = null;
    }
}
