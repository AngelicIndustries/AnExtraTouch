package industries.angelic.anextratouch.varinstances.configcaches;

import static org.junit.Assert.*;

import java.util.Set;

import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.world.World;

import org.junit.Test;

public class EntityClassListTest {

    /** Like Special Mobs' blazes, which extend the vanilla blaze. */
    private static class VariantBlaze extends EntityBlaze {

        VariantBlaze(World world) {
            super(world);
        }
    }

    @Test
    public void anEntryCoversModdedVariantsOfItsMob() {
        Set<Class<?>> listed = EntityClassList.classes(new String[] { "Blaze", "SomeMissingMod.Mob" });
        assertEquals(1, listed.size());
        assertTrue(EntityClassList.listed(EntityBlaze.class, listed));
        assertTrue(EntityClassList.listed(VariantBlaze.class, listed));
        assertFalse(EntityClassList.listed(EntityGhast.class, listed));
    }
}
