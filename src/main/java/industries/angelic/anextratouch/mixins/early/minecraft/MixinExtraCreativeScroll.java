package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainerCreative;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.extrasounds.ScrollSounds;

@Mixin(GuiContainerCreative.class)
public abstract class MixinExtraCreativeScroll {

    @Shadow
    private float currentScroll;
    @Unique
    private float anextratouch$before;
    @Unique
    private final ScrollSounds anextratouch$scroll = new ScrollSounds();

    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    private void anextratouch$beforeWheel(CallbackInfo ci) {
        anextratouch$before = currentScroll;
    }

    @Inject(method = "handleMouseInput", at = @At("RETURN"))
    private void anextratouch$afterWheel(CallbackInfo ci) {
        anextratouch$scroll();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void anextratouch$beforeDrag(int x, int y, float partialTicks, CallbackInfo ci) {
        anextratouch$before = currentScroll;
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void anextratouch$afterDrag(int x, int y, float partialTicks, CallbackInfo ci) {
        anextratouch$scroll();
    }

    @Unique
    private void anextratouch$scroll() {
        int count = ((AccessorExtraCreativeContainer) ((GuiContainerCreative) (Object) this).inventorySlots)
            .anextratouch$items()
            .size();
        int rows = Math.max(0, count / 9 - 5 + 1);
        anextratouch$scroll.play(Math.round(anextratouch$before * rows), Math.round(currentScroll * rows));
    }
}
