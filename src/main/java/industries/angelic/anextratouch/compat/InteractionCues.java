package industries.angelic.anextratouch.compat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
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
    private static final Queue<Runnable> pending = new ConcurrentLinkedQueue<>();
    private static volatile Thread serverThread;
    /** Cut like wheat. */
    private static final Set<String> GRAINS = new HashSet<>(
        Arrays.asList("wheat", "barley", "oats", "rye", "rice", "sesameseeds", "mustardseeds", "abyssaloat"));
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
            "chilipepper",
            "marrowberry",
            "ignisfruit"));
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
            "leafygarden",
            "bloodleaf"));

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
        HARVEST_LEAFY("action.harvest.leafy"),
        /** A sink rinsing an item, or running water over an empty hand. */
        SINK_RINSE("action.sink.rinse"),
        /** A tank emptied by its drain or discard button. */
        DRAIN("action.drain"),
        TANK_SWITCH("action.tank.switch"),
        PURCHASE("action.purchase"),
        COOK_CRAFT("action.cook.craft"),
        COOK_SMELT("action.cook.smelt"),
        LUNCHBOX_OPEN("action.lunchbox.open"),
        LUNCHBOX_CLOSE("action.lunchbox.close"),
        LUNCHBAG_OPEN("action.lunchbag.open"),
        LUNCHBAG_CLOSE("action.lunchbag.close"),
        /** Twilight Forest's magic log cores switching on or off. */
        CORE_ON("action.core.on", 0.1F),
        CORE_OFF("action.core.off", 0.1F),
        /** A Twilight Forest critter going onto a player's head. */
        CRITTER("action.critter.pickup"),
        /** A creature let out of a bucket into water, like a modern fish bucket. */
        BUCKET_RELEASE("action.bucket.release"),
        /** A caught bug let go. */
        BUG_RELEASE("action.bug.release"),
        /** A seed planted as a creature or entity rather than a block. */
        PLANT("action.plant"),
        /** Wild Mobs' mired bottle uncorked to summon a mired. */
        MIRED("action.mired.summon");

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

    /** At the block in front of the clicked face, where something used on a block appears. */
    public static void sendOnFace(World world, int x, int y, int z, int side, Cue cue) {
        if (side < 0 || side >= Facing.offsetsXForSide.length) send(world, x, y, z, cue);
        else send(
            world,
            x + Facing.offsetsXForSide[side],
            y + Facing.offsetsYForSide[side],
            z + Facing.offsetsZForSide[side],
            cue);
    }

    public static void send(World world, int x, int y, int z, Cue cue) {
        if (world == null || world.isRemote || cue == null) return;
        int dimension = world.provider.dimensionId;
        Runnable send = () -> {
            AnExtraTouch.debug("Interaction cue " + cue + " (" + cue.event + ") sent at " + x + ", " + y + ", " + z);
            NetworkHandler.channel.sendToAllAround(
                new MessageInteractionCue(cue, x, y, z),
                new NetworkRegistry.TargetPoint(dimension, x + 0.5, y + 0.5, z + 0.5, RANGE));
        };
        // Mod packet handlers run on network threads; sending from there would race the server thread.
        if (Thread.currentThread() == serverThread) send.run();
        else pending.add(send);
    }

    /** Sends cues raised off the server thread on its next tick. */
    public static final class Ticker {

        @SubscribeEvent
        public void tick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            serverThread = Thread.currentThread();
            for (Runnable send; (send = pending.poll()) != null;) send.run();
        }
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

    /** Twilight Forest's magic log cores are off while their orientation bits hold 8 (4 and 8 are the time core's). */
    public static Cue core(int meta) {
        return (meta & 8) != 0 ? Cue.CORE_OFF : Cue.CORE_ON;
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

    /**
     * HarvestCraft's crop blocks are named {@code pam<crop>Crop} and Fether's {@code <crop>_crop}; gardens, fruit and
     * vanilla crops keep their own names.
     */
    public static Cue crop(String registryName) {
        if (registryName == null) return Cue.HARVEST_CROP;
        String crop = registryName.substring(registryName.indexOf(':') + 1);
        if (crop.startsWith("pam")) crop = crop.substring(3);
        if (crop.endsWith("Crop")) crop = crop.substring(0, crop.length() - 4);
        crop = crop.toLowerCase(Locale.ROOT);
        if (crop.endsWith("_crop")) crop = crop.substring(0, crop.length() - 5);
        crop = crop.replace("_", "");
        if (FRUIT.contains(crop)) return Cue.HARVEST;
        if (GRAINS.contains(crop)) return Cue.HARVEST_GRAIN;
        return LEAFY.contains(crop) ? Cue.HARVEST_LEAFY : Cue.HARVEST_CROP;
    }

    public static ItemStack copy(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }
}
