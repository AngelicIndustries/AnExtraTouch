package industries.angelic.anextratouch.compat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;

import cpw.mods.fml.common.network.NetworkRegistry;
import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.network.NetworkHandler;
import industries.angelic.anextratouch.network.message.MessageInteractionCue;

/**
 * Mod world interactions that play no sound of their own and often happen only on the server. The server sends the cue
 * to nearby players, whose own settings decide whether it plays.
 */
public final class InteractionCues {

    /** Vanilla's usual range for block sounds. */
    static final double RANGE = 16;
    /** Cut like wheat. */
    private static final Set<String> GRAINS = new HashSet<>(
        Arrays.asList("wheat", "barley", "oats", "rye", "rice", "sesameseeds", "mustardseeds"));
    /** Ground crops whose produce is picked like fruit. */
    private static final Set<String> FRUIT = new HashSet<>(
        Arrays.asList(
            "blackberry",
            "blueberry",
            "raspberry",
            "strawberry",
            "cranberry",
            "candleberry",
            "tomato",
            "bellpepper",
            "chilipepper"));
    /** Torn off like lettuce. */
    private static final Set<String> LEAFY = new HashSet<>(
        Arrays.asList(
            "lettuce",
            "cabbage",
            "spinach",
            "broccoli",
            "cauliflower",
            "brusselsprout",
            "celery",
            "rhubarb",
            "spiceleaf",
            "curryleaf",
            "tealeaf",
            "seaweed",
            "leafygarden"));

    public enum Cue {

        BUCKET_FILL("action.bucket.fill"),
        BUCKET_EMPTY("action.bucket.empty"),
        BOTTLE_FILL("action.bottle.fill"),
        BOTTLE_EMPTY("action.bottle.empty"),
        TILL("action.hoe.till"),
        /** Picking fruit, berries, hops, grapes or curds. */
        HARVEST("action.harvest"),
        CHURN("action.churn", 0.4F),
        CHURN_DONE("action.churn.done", 0.4F),
        /** Crops that are neither grain nor leafy, and gardens. */
        HARVEST_CROP("action.harvest.crop"),
        HARVEST_GRAIN("action.harvest.grain"),
        HARVEST_LEAFY("action.harvest.leafy");

        public final String event;
        /** Random pitch range around the event's own pitch. */
        public final float spread;

        Cue(String event) {
            this(event, 0.2F);
        }

        Cue(String event, float spread) {
            this.event = event;
            this.spread = spread;
        }

        public static Cue byId(int id) {
            Cue[] cues = values();
            return id >= 0 && id < cues.length ? cues[id] : null;
        }
    }

    private InteractionCues() {}

    public static void send(World world, int x, int y, int z, Cue cue) {
        if (world == null || world.isRemote || cue == null) return;
        AnExtraTouch.debug("Interaction cue " + cue + " (" + cue.event + ") sent at " + x + ", " + y + ", " + z);
        NetworkHandler.channel.sendToAllAround(
            new MessageInteractionCue(cue, x, y, z),
            new NetworkRegistry.TargetPoint(world.provider.dimensionId, x + 0.5, y + 0.5, z + 0.5, RANGE));
    }

    /** Filling or emptying a container, judged by what was held before the action. */
    public static Cue container(ItemStack held, boolean filled) {
        return container(isBucket(held), filled);
    }

    static Cue container(boolean bucket, boolean filled) {
        if (filled) return bucket ? Cue.BUCKET_FILL : Cue.BOTTLE_FILL;
        return bucket ? Cue.BUCKET_EMPTY : Cue.BOTTLE_EMPTY;
    }

    static boolean isBucket(ItemStack held) {
        return held != null && (held.getItem() == Items.bucket || FluidContainerRegistry.isBucket(held));
    }

    /** Right-click harvesting changes the plant it was used on, or removes it. */
    public static boolean changed(Block before, int metaBefore, Block after, int metaAfter) {
        return before != after || metaBefore != metaAfter;
    }

    /** A crop harvested by right-click, by its registry name and its metadata before harvest. */
    public static Cue crop(String registryName, int meta) {
        // Natura's crop block holds barley in metadata 0-3 and cotton above.
        if ("Natura:N Crops".equals(registryName)) return meta <= 3 ? Cue.HARVEST_GRAIN : Cue.HARVEST_CROP;
        return crop(registryName);
    }

    /** HarvestCraft's crop blocks are named {@code pam<crop>Crop}; gardens and vanilla crops keep their own names. */
    public static Cue crop(String registryName) {
        if (registryName == null) return Cue.HARVEST_CROP;
        String crop = registryName.substring(registryName.indexOf(':') + 1);
        if (crop.startsWith("pam")) crop = crop.substring(3);
        if (crop.endsWith("Crop")) crop = crop.substring(0, crop.length() - 4);
        crop = crop.toLowerCase(Locale.ROOT);
        if (FRUIT.contains(crop)) return Cue.HARVEST;
        if (GRAINS.contains(crop)) return Cue.HARVEST_GRAIN;
        return LEAFY.contains(crop) ? Cue.HARVEST_LEAFY : Cue.HARVEST_CROP;
    }

    public static ItemStack copy(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }
}
