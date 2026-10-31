package industries.angelic.anextratouch.handlers.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;

/** Inventory pages and NEI recipe views do not represent a new inventory session. */
public final class PlayerInventoryScreens {

    private PlayerInventoryScreens() {}

    public static boolean isNavigation(GuiScreen from, GuiScreen to) {
        if (isRecipeView(to)) return true;
        return from != null && to != null && (isRecipeView(from) || isPlayerInventory(from) && isPlayerInventory(to));
    }

    private static boolean isRecipeView(GuiScreen screen) {
        // Crafting and usage views subclass GuiRecipe. Do not load NEI types when it is absent.
        for (Class<?> type = screen == null ? null : screen.getClass(); type != null; type = type.getSuperclass()) {
            if ("codechicken.nei.recipe.GuiRecipe".equals(type.getName())) return true;
        }
        return false;
    }

    private static boolean isPlayerInventory(GuiScreen screen) {
        if (screen instanceof GuiInventory) return true;
        // Match by name so Baubles remains optional.
        return screen != null && "baubles.client.gui.GuiPlayerExpanded".equals(
            screen.getClass()
                .getName());
    }
}
