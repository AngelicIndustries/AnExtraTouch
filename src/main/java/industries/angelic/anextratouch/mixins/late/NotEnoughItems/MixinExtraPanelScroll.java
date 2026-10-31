package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.ItemsGrid;
import codechicken.nei.PanelWidget;
import industries.angelic.anextratouch.handlers.client.extrasounds.ScrollSounds;

@Mixin(value = PanelWidget.class, remap = false)
public abstract class MixinExtraPanelScroll {

    @Shadow
    public abstract ItemsGrid<?, ?> getGrid();

    @Unique
    private final ScrollSounds anextratouch$scroll = new ScrollSounds();

    @WrapMethod(method = "onMouseWheel")
    private boolean anextratouch$mouseWheel(int direction, int x, int y, Operation<Boolean> original) {
        int before = getGrid().getPage();
        boolean handled = original.call(direction, x, y);
        // Page buttons, hotkeys and layout refreshes do not pass through this wheel handler.
        anextratouch$scroll.play(before, getGrid().getPage());
        return handled;
    }
}
