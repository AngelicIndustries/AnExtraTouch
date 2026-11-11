package industries.angelic.anextratouch.compat;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import industries.angelic.anextratouch.Config;

public class ThrowSoundsTest {

    @Test
    public void onlyTheBorrowedBowReleaseBecomesAThrow() {
        boolean enabled = Config.itemThrowSoundsEnabled;
        try {
            Config.itemThrowSoundsEnabled = true;
            assertEquals("anextratouch:item.throw", ThrowSounds.replace("random.bow"));
            assertEquals("random.pop", ThrowSounds.replace("random.pop"));
            Config.itemThrowSoundsEnabled = false;
            assertEquals("random.bow", ThrowSounds.replace("random.bow"));
        } finally {
            Config.itemThrowSoundsEnabled = enabled;
        }
    }

    @Test
    public void theThrowKeepsItsOwnPitch() {
        Random random = new Random(7);
        for (int i = 0; i < 200; i++) {
            float pitch = ThrowSounds.pitch(random);
            assertTrue(pitch >= 0.9F && pitch <= 1.1F);
        }
    }
}
