package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.scroll.ScrollContainer;
import industries.angelic.anextratouch.handlers.client.extrasounds.ScrollSounds;

@Mixin(value = ScrollContainer.class, remap = false)
public abstract class MixinExtraScrollContainer {

    @Shadow
    protected int xScroll;

    @Shadow
    protected int yScroll;

    @Unique
    private final ScrollSounds anextratouch$scroll = new ScrollSounds();

    @WrapMethod(method = { "onMouseWheel", "handleClick" })
    private boolean anextratouch$mouseInput(int first, int second, int third, Operation<Boolean> original) {
        int x = xScroll, y = yScroll;
        boolean handled = original.call(first, second, third);
        anextratouch$scroll(x, y);
        return handled;
    }

    @WrapMethod(method = "mouseDragged")
    private void anextratouch$mouseDragged(int x, int y, int button, long heldTime, Operation<Void> original) {
        int beforeX = xScroll, beforeY = yScroll;
        original.call(x, y, button, heldTime);
        anextratouch$scroll(beforeX, beforeY);
    }

    @WrapMethod(method = "dragScrollBars")
    private void anextratouch$dragScrollBars(int x, int y, boolean vertical, boolean horizontal,
        Operation<Void> original) {
        int beforeX = xScroll, beforeY = yScroll;
        original.call(x, y, vertical, horizontal);
        anextratouch$scroll(beforeX, beforeY);
    }

    @Unique
    private void anextratouch$scroll(int x, int y) {
        // NEI moves 16 pixels per wheel step. Quantize dragging to the same steps.
        if (x / 16 != xScroll / 16) anextratouch$scroll.play(x / 16, xScroll / 16);
        else anextratouch$scroll.play(y / 16, yScroll / 16);
    }
}
