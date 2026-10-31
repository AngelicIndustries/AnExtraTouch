package industries.angelic.anextratouch.handlers.client;

import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;

public class SmoothGuiHandler {

    private static final float FADE_OFFSET = 9f;

    private static long lastScreenOpenedTime = 0;
    private static long lastScreenChangedTime = 0;
    private static float appliedDisplacement = 0f;
    private static boolean matrixPushed = false;

    public static float getAppliedDisplacement() {
        return appliedDisplacement;
    }

    public static float getAlphaSince(long time) {
        return getAlphaSince(time, Config.smoothGuiAnimationTime);
    }

    public static float getAlphaSince(long time, int durationMs) {
        float fadeTime = Math.max(1, durationMs);
        float elapsed = Math.min((float) (System.currentTimeMillis() - time), fadeTime);
        return elapsed / fadeTime;
    }

    public static long getLastScreenOpenedTime() {
        return lastScreenOpenedTime;
    }

    private static float applyEasing(float t) {
        if ("CUBIC".equalsIgnoreCase(Config.smoothGuiAnimationStyle)) {
            return t * t * t;
        }
        // BACK (default)
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return c3 * t * t * t - c1 * t * t;
    }

    private static float calculateDisplacement() {
        float alpha = getAlphaSince(lastScreenChangedTime);
        float directionMod = "UP".equalsIgnoreCase(Config.smoothGuiAnimationDirection) ? -1f : 1f;
        return FADE_OFFSET * applyEasing(1f - alpha) * Config.smoothGuiAnimationScale * directionMod;
    }

    private static boolean isValidScreen(GuiScreen screen) {
        if (screen == null) {
            return false;
        }
        HashSet<String> excluded = AnExtraTouch.vic.smoothGuiExcludedScreens;
        if (excluded == null) {
            return true;
        }
        return !excluded.contains(
            screen.getClass()
                .getSimpleName())
            && !excluded.contains(
                screen.getClass()
                    .getName());
    }

    private static boolean isInMainMenu() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld == null && mc.thePlayer == null;
    }

    private static boolean isPlayerInventory(GuiScreen screen) {
        if (screen instanceof GuiInventory) return true;
        // Match by name so Baubles remains optional.
        return screen != null && "baubles.client.gui.GuiPlayerExpanded".equals(
            screen.getClass()
                .getName());
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        GuiScreen newScreen = event.gui;
        GuiScreen oldScreen = Minecraft.getMinecraft().currentScreen;
        if (newScreen != null) {
            long now = System.currentTimeMillis();
            if (oldScreen != null && oldScreen.getClass()
                .equals(newScreen.getClass())) {
                return;
            }
            // The Baubles button switches pages of the same inventory.
            if (isPlayerInventory(oldScreen) && isPlayerInventory(newScreen)) {
                return;
            }
            lastScreenChangedTime = now;
            if (oldScreen == null) {
                lastScreenOpenedTime = now;
            }
        }
    }

    @SubscribeEvent
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        // Recover from missed Post events (e.g., canceled draw path) to avoid matrix stack leaks.
        if (matrixPushed) {
            GL11.glPopMatrix();
            matrixPushed = false;
            appliedDisplacement = 0f;
        }
        if (!Config.smoothGuiEnabled) {
            return;
        }
        if (!isValidScreen(event.gui)) {
            return;
        }
        if (isInMainMenu()) return;
        appliedDisplacement = calculateDisplacement();
        if (appliedDisplacement == 0f) {
            return;
        }
        GL11.glPushMatrix();
        matrixPushed = true;
        GL11.glTranslatef(0f, -appliedDisplacement, 0f);
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!matrixPushed) {
            return;
        }
        GL11.glPopMatrix();
        matrixPushed = false;
        appliedDisplacement = 0f;
    }
}
