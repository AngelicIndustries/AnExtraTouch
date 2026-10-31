package industries.angelic.anextratouch.handlers.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;

/** The regular and Baubles screens are pages of the same player inventory. */
public final class PlayerInventoryScreens {

    private PlayerInventoryScreens() {}

    public static boolean isPageSwitch(GuiScreen from, GuiScreen to) {
        return isPlayerInventory(from) && isPlayerInventory(to);
    }

    private static boolean isPlayerInventory(GuiScreen screen) {
        if (screen instanceof GuiInventory) return true;
        // Match by name so Baubles remains optional.
        return screen != null && "baubles.client.gui.GuiPlayerExpanded".equals(
            screen.getClass()
                .getName());
    }
}
