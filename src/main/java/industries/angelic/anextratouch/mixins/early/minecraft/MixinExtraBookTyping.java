package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.TypingSounds;

@Mixin(GuiScreenBook.class)
public abstract class MixinExtraBookTyping {

    @Shadow
    private String bookTitle;
    @Shadow
    private NBTTagList bookPages;
    @Shadow
    private int currPage;
    @Shadow
    private boolean bookIsUnsigned;
    @Shadow
    private boolean field_146480_s;
    @Unique
    private String anextratouch$text;

    @Unique
    private String anextratouch$text() {
        return field_146480_s ? bookTitle
            : bookPages != null && currPage < bookPages.tagCount() ? bookPages.getStringTagAt(currPage) : "";
    }

    @Inject(method = "keyTyped", at = @At("HEAD"))
    private void anextratouch$before(char c, int key, CallbackInfo ci) {
        anextratouch$text = Config.extraTyping && bookIsUnsigned ? anextratouch$text() : null;
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void anextratouch$after(char c, int key, CallbackInfo ci) {
        TypingSounds.changed(anextratouch$text, anextratouch$text(), 0, 0, 0, 0, c, key, GuiScreen.isCtrlKeyDown());
        anextratouch$text = null;
    }
}
