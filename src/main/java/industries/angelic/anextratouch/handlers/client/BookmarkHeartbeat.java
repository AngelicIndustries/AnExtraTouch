package industries.angelic.anextratouch.handlers.client;

/** Two eased beats, timed independently of the render frame rate. */
public final class BookmarkHeartbeat {

    public static final long DURATION_NANOS = 820000000L;
    private static final float[] TIMES = { 0, 0.14F, 0.27F, 0.40F, 0.54F, 0.82F };
    private static final float[] SCALES = { 0.55F, 1.45F, 0.95F, 1.25F, 1.0F, 0.9F };

    private BookmarkHeartbeat() {}

    public static float scale(long elapsed) {
        float seconds = Math.max(0, elapsed) / 1000000000F;
        for (int i = 1; i < TIMES.length; i++) {
            if (seconds <= TIMES[i]) {
                float t = smooth((seconds - TIMES[i - 1]) / (TIMES[i] - TIMES[i - 1]));
                return SCALES[i - 1] + (SCALES[i] - SCALES[i - 1]) * t;
            }
        }
        return SCALES[SCALES.length - 1];
    }

    public static float alpha(long elapsed) {
        float seconds = elapsed / 1000000000F;
        return smooth(seconds / 0.07F) * (1 - smooth((seconds - 0.54F) / 0.28F));
    }

    private static float smooth(float t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3 - 2 * t);
    }
}
