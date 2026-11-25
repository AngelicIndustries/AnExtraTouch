package industries.angelic.anextratouch.mixins.late.projectzulu;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.TypingSounds;

/** Project Zulu's animal naming screen edits its own string instead of a text field, so typing was silent. */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.blocks.GuiAnimalName", remap = false)
public abstract class MixinZuluNameTyping {

    @Shadow
    public String drawStr;
    @Unique
    private String anextratouch$text;

    @Inject(method = { "keyTyped", "func_73869_a" }, at = @At("HEAD"))
    private void anextratouch$before(char c, int key, CallbackInfo ci) {
        anextratouch$text = Config.extraTyping ? drawStr : null;
    }

    @Inject(method = { "keyTyped", "func_73869_a" }, at = @At("RETURN"))
    private void anextratouch$after(char c, int key, CallbackInfo ci) {
        // Text is only appended or erased at the end, so the cursor is its length.
        String before = anextratouch$text;
        anextratouch$text = null;
        if (before == null || drawStr == null) return;
        TypingSounds.changed(
            before,
            drawStr,
            before.length(),
            drawStr.length(),
            before.length(),
            drawStr.length(),
            c,
            key,
            GuiScreen.isCtrlKeyDown());
    }
}
