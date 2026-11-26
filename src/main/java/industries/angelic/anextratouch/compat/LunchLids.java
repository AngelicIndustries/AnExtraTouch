package industries.angelic.anextratouch.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

/** Spice of Life's lunch box and bag keep their lid state in an {@code Open} NBT flag. */
public final class LunchLids {

    private LunchLids() {}

    public static boolean open(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .getBoolean("Open");
    }

    /** Plays the lid cue when the flag changed. Server side. */
    public static void changed(EntityPlayer player, ItemStack stack, boolean wasOpen) {
        boolean open = open(stack);
        if (player.worldObj.isRemote || open == wasOpen) return;
        String name = Item.itemRegistry.getNameForObject(stack.getItem());
        boolean box = name != null && name.endsWith("lunchbox");
        InteractionCues.Cue cue = box ? open ? InteractionCues.Cue.LUNCHBOX_OPEN : InteractionCues.Cue.LUNCHBOX_CLOSE
            : open ? InteractionCues.Cue.LUNCHBAG_OPEN : InteractionCues.Cue.LUNCHBAG_CLOSE;
        InteractionCues.send(
            player.worldObj,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ),
            cue);
    }
}
