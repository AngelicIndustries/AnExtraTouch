package industries.angelic.anextratouch.handlers.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class BookmarkHeartbeatTest {

    @Test
    public void hasTwoBeatsWithASmallerSecondPeak() {
        assertTrue(BookmarkHeartbeat.scale(140000000) > BookmarkHeartbeat.scale(270000000));
        assertTrue(BookmarkHeartbeat.scale(400000000) > BookmarkHeartbeat.scale(270000000));
        assertTrue(BookmarkHeartbeat.scale(140000000) > BookmarkHeartbeat.scale(400000000));
        assertTrue(BookmarkHeartbeat.scale(400000000) > BookmarkHeartbeat.scale(540000000));
    }

    @Test
    public void fadesInAndOutWithoutOvershootOrJumps() {
        assertEquals(0, BookmarkHeartbeat.alpha(0), 0);
        assertEquals(1, BookmarkHeartbeat.alpha(140000000), 0);
        assertEquals(0, BookmarkHeartbeat.alpha(BookmarkHeartbeat.DURATION_NANOS), 0.00001F);
        assertEquals(0, BookmarkHeartbeat.alpha(2000000000L), 0);
        float previous = BookmarkHeartbeat.scale(0);
        for (long t = 0; t <= BookmarkHeartbeat.DURATION_NANOS; t += 1000000) {
            float scale = BookmarkHeartbeat.scale(t), alpha = BookmarkHeartbeat.alpha(t);
            assertTrue(scale >= 0.55F && scale <= 1.45F);
            assertTrue(alpha >= 0 && alpha <= 1);
            assertTrue(Math.abs(scale - previous) < 0.02F);
            previous = scale;
        }
    }
}
