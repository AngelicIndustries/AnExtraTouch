package industries.angelic.anextratouch.mixins.late.SpiceOfLife;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import industries.angelic.anextratouch.handlers.client.extrasounds.BookPages;

/** The food journal copies the vanilla book's page arrows, so they turn a page too. */
@Pseudo
@Mixin(targets = "squeek.spiceoflife.gui.widget.WidgetButtonNextPage", remap = false)
public abstract class MixinJournalPageButton extends GuiButton {

    private MixinJournalPageButton() {
        super(0, 0, 0, "");
    }

    @Override
    public void func_146113_a(SoundHandler handler) {
        if (!BookPages.turn()) super.func_146113_a(handler);
    }
}
