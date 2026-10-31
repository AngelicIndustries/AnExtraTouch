package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.minecraft.client.audio.PositionedSoundRecord;

import org.junit.Test;

public class UiSoundSourcesTest {

    @Test
    public void uiMarkerSurvivesSampleOverridesAndResumeButNotCleanup() {
        String source = "ui-source";
        UiSoundSources.register(source, new ExtraSounds.UiSound(ExtraSounds.id("item.pickup.minecraft.stone"), 1, 1));
        assertTrue(UiSoundSources.isRelative(source));
        // The same source is played again when resuming after a pause.
        assertTrue(UiSoundSources.isRelative(source));
        UiSoundSources.remove(source);
        assertFalse(UiSoundSources.isRelative(source));
    }

    @Test
    public void worldSoundsInTheSameNamespaceRemainPositional() {
        String source = "world-source";
        UiSoundSources.register(source, new PositionedSoundRecord(ExtraSounds.id("entity.poof"), 1, 1, 10, 20, 30));
        assertFalse(UiSoundSources.isRelative(source));
        UiSoundSources.remove(source);
    }
}
