package industries.angelic.anextratouch.compat;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import industries.angelic.anextratouch.compat.adventurebackpack.BackpackSounds;

/** Optional entry points; the implementation is linked only when Adventure Backpack is installed. */
public final class AdventureBackpackCompat {

    private static Boolean available;

    private AdventureBackpackCompat() {}

    private static boolean available() {
        if (available == null) available = Loader.isModLoaded("adventurebackpack");
        return available;
    }

    public static boolean isWornScreen(GuiScreen screen) {
        return available() && BackpackSounds.isWornScreen(screen);
    }

    public static void slots(int window, int start, ItemStack[] stacks) {
        if (available()) BackpackSounds.slots(window, start, stacks);
    }

    public static void sent(int window, short transaction) {
        if (available()) BackpackSounds.sent(window, transaction);
    }

    public static void acknowledged(int window, short transaction, boolean accepted) {
        if (available()) BackpackSounds.acknowledged(window, transaction, accepted);
    }

    public static boolean takeFluidClick() {
        return available() && BackpackSounds.takeFluidClick();
    }

    public static void tick() {
        if (available()) BackpackSounds.tick();
    }
}
