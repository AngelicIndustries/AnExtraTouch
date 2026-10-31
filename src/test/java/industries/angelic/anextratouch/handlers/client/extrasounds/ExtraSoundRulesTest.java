package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExtraSoundRulesTest {

    @Test
    public void typingOnlyReportsAcceptedChanges() {
        assertNull(edit("abc", "abc", 3, 3, 'x', 45, false));
        assertNull(edit("", "", 0, 0, '\b', 14, false));
        assertEquals("erase", edit("abc", "ab", 3, 2, '\b', 14, false));
        assertEquals("move", edit("abc", "abc", 3, 2, '\0', 203, false));
        assertEquals("type", edit("abc", "abX", 3, 3, 'X', 45, false));
        assertEquals("paste", edit("abc", "hello", 3, 5, (char) 22, 47, true));
        assertEquals("cut", edit("abc", "", 3, 0, (char) 24, 45, true));
        assertEquals("move", edit("abc", "abc\n", 3, 4, '\n', 28, false));
        assertEquals("move", TypingSounds.action("abc", "abc", 3, 3, 3, 0, (char) 1, 30, true));
    }

    private static String edit(String before, String after, int oldCursor, int cursor, char c, int key, boolean ctrl) {
        return TypingSounds.action(before, after, oldCursor, cursor, oldCursor, cursor, c, key, ctrl);
    }

    @Test
    public void controlShortcutsClassifyAcceptedChangesAndIgnoreRepeatedSelection() {
        // Hodgepodge handles physical shortcut keys even without a control character.
        assertEquals("move", TypingSounds.action("abc", "abc", 3, 3, 3, 0, '\0', 30, true));
        assertNull(TypingSounds.action("abc", "abc", 3, 3, 0, 0, '\0', 30, true));
        assertNull(TypingSounds.action("abc", "abc", 3, 3, 0, 0, '\0', 46, true));
        assertEquals("cut", TypingSounds.action("abc", "", 3, 0, 0, 0, '\0', 45, true));
        assertEquals("paste", TypingSounds.action("", "abc", 0, 3, 0, 3, '\0', 47, true));
    }

    @Test
    public void mentionsMatchNamesNotSubstringsOrOwnMessages() {
        assertTrue(ExtraSoundHandler.mention("<Alex> hey @Jack!", "jack", false));
        assertTrue(ExtraSoundHandler.mention("Jack, over here", "Jack", false));
        assertFalse(ExtraSoundHandler.mention("<Jack> hi", "Jack", true));
        assertFalse(ExtraSoundHandler.mention("jackson and jack_2", "jack", false));
    }

    @Test
    public void dropPitchUsesAmountActuallyDropped() {
        assertEquals(2, ExtraSounds.dropPitch(1, 64), 0);
        assertEquals(1.5, ExtraSounds.dropPitch(64, 64), 0.00001);
        assertEquals(2, ExtraSounds.dropPitch(1, 1), 0);
        assertEquals(1.75, ExtraSounds.dropPitch(3, 5), 0);
    }

    @Test
    public void scrollOnlySoundsForNewRowsAndLimitsFastScroll() {
        ScrollSounds scroll = new ScrollSounds();
        assertEquals(0, scroll.moved(0, 0, 100), 0);
        assertEquals(0.9, scroll.moved(0, 1, 100), 0.00001);
        assertEquals(0, scroll.moved(1, 2, 110), 0);
        assertEquals(1, scroll.moved(2, 3, 130), 0);
        assertEquals(0.95, scroll.moved(3, 4, 230), 0.00001);
    }
}
