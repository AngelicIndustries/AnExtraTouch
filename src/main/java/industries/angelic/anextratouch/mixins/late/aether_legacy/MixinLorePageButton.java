package industries.angelic.anextratouch.mixins.late.aether_legacy;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import industries.angelic.anextratouch.handlers.client.extrasounds.BookPages;

/** The Aether lore book's page arrows, its only use of this button, turn a page instead of clicking. */
@Pseudo
@Mixin(targets = "com.gildedgames.the_aether.client.gui.button.GuiLoreButton", remap = false)
public abstract class MixinLorePageButton extends GuiButton {

    private MixinLorePageButton() {
        super(0, 0, 0, "");
    }

    @Override
    public void func_146113_a(SoundHandler handler) {
        if (!BookPages.turn()) super.func_146113_a(handler);
    }
}
