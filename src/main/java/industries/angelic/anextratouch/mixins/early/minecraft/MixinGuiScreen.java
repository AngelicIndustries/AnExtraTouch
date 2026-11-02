package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.SmoothGuiHandler;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {

    // Fade only the vanilla draw call. Replacing the entire method prevents mods such as Blur
    // from supplying their own background and transition.
    @WrapOperation(
        method = "drawWorldBackground",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V"),
        require = 1)
    private void anextratouch$fadeBackground(GuiScreen screen, int left, int top, int right, int bottom, int startColor,
        int endColor, Operation<Void> original) {
        if (!Config.smoothGuiEnabled || !Config.smoothGuiFadeBackground) {
            original.call(screen, left, top, right, bottom, startColor, endColor);
            return;
        }
        float fade = SmoothGuiHandler
            .getAlphaSince(SmoothGuiHandler.getLastScreenOpenedTime(), Config.smoothGuiBackgroundFadeTime);
        int fadedStart = anextratouch$applyFade(startColor, fade);
        int fadedEnd = anextratouch$applyFade(endColor, fade);

        // undo the slide displacement so the background stays fixed (matching og mod)
        float displacement = SmoothGuiHandler.getAppliedDisplacement();
        if (displacement != 0f) {
            GL11.glTranslatef(0f, displacement, 0f);
        }
        try {
            original.call(screen, left, top, right, bottom, fadedStart, fadedEnd);
        } finally {
            if (displacement != 0f) {
                GL11.glTranslatef(0f, -displacement, 0f);
            }
        }
    }

    @Unique
    private static int anextratouch$applyFade(int color, float fade) {
        int a = (int) (((color >> 24) & 0xFF) * fade);
        return (a << 24) | (color & 0x00FFFFFF);
    }
}
