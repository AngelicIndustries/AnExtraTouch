package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

import org.spongepowered.asm.mixin.Mixin;

import industries.angelic.anextratouch.handlers.client.extrasounds.BookPages;

/** Vanilla book screens' page arrows turn a page instead of clicking. */
@Mixin(targets = "net.minecraft.client.gui.GuiScreenBook$NextPageButton")
public abstract class MixinBookPageButton extends GuiButton {

    private MixinBookPageButton() {
        super(0, 0, 0, "");
    }

    @Override
    public void func_146113_a(SoundHandler handler) {
        if (!BookPages.turn()) super.func_146113_a(handler);
    }
}
