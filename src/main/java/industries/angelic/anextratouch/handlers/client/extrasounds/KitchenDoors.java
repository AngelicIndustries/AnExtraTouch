package industries.angelic.anextratouch.handlers.client.extrasounds;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;

import industries.angelic.anextratouch.Config;

/**
 * Cooking for Blockheads' fridges, ovens, counters and cabinets swing their doors like a chest lid, on every nearby
 * client. The door sounds when it starts to open and when it shuts, and replaces the generic screen sounds.
 */
public final class KitchenDoors {

    public enum Move {
        OPEN,
        CLOSE
    }

    private KitchenDoors() {}

    static Move move(float before, float now) {
        if (before == 0 && now > 0) return Move.OPEN;
        return before > 0 && now == 0 ? Move.CLOSE : null;
    }

    public static void ticked(TileEntity tile, float before, float now) {
        if (tile.getWorldObj() == null || !tile.getWorldObj().isRemote || !Config.extraModInteractions) return;
        Move move = move(before, now);
        if (move == null) return;
        String event = "door." + kind(tile) + (move == Move.OPEN ? ".open" : ".close");
        float pitch = 0.95F + (float) Math.random() * 0.1F;
        ExtraSounds.at(event, tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5, 1, pitch);
    }

    static String kind(TileEntity tile) {
        String name = tile.getClass()
            .getSimpleName();
        if ("TileOven".equals(name)) return "oven";
        return "TileFridge".equals(name) ? "fridge" : "cabinet";
    }

    /** The screens of door-swinging kitchen storage, whose doors already sound. */
    public static boolean ownsScreen(GuiScreen screen) {
        if (screen == null || !Config.extraModInteractions) return false;
        String name = screen.getClass()
            .getName();
        return "net.blay09.mods.cookingforblockheads.client.GuiInventory".equals(name)
            || "net.blay09.mods.cookingforblockheads.client.GuiCookingOven".equals(name);
    }
}
