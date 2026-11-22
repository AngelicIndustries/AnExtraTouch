package industries.angelic.anextratouch.compat;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;

/**
 * Thrown items (potions, snowballs, eggs, ender pearls, bottles o' enchanting) borrow vanilla's bow release at a very
 * low
 * pitch, which resource packs replacing {@code random.bow} turn strange. They play modern Minecraft's witch potion
 * throw
 * instead, at its own pitch.
 */
public final class ThrowSounds {

    public static final String THROW = AnExtraTouch.MODID + ":item.throw";
    public static final String BLOWGUN = AnExtraTouch.MODID + ":item.blowgun.shoot";

    private ThrowSounds() {}

    public static float pitch(Random random) {
        return 0.9F + random.nextFloat() * 0.2F;
    }

    /**
     * Wraps a thrown item's {@code World.playSoundAtEntity} call; anything but the borrowed bow release passes through.
     */
    public static void thrown(World world, Entity thrower, String sound, float volume, float pitch,
        Operation<Void> original) {
        String played = replace(sound);
        original.call(world, thrower, played, volume, played.equals(sound) ? pitch : pitch(world.rand));
    }

    /** Mowzie's Mobs' blowgun fires its darts with the same bow release; it puffs instead. */
    public static void blown(World world, Entity shooter, String sound, float volume, float pitch,
        Operation<Void> original) {
        String played = blowgun(sound);
        original.call(world, shooter, played, volume, played.equals(sound) ? pitch : pitch(world.rand));
    }

    static String blowgun(String sound) {
        return Config.itemThrowSoundsEnabled && "random.bow".equals(sound) ? BLOWGUN : sound;
    }

    static String replace(String sound) {
        return Config.itemThrowSoundsEnabled && "random.bow".equals(sound) ? THROW : sound;
    }
}
