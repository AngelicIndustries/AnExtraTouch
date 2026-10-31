package industries.angelic.anextratouch.handlers.client.extrasounds;

import industries.angelic.anextratouch.Config;

/** Extra Sounds' 20 ms scroll limiter and speed-dependent pitch. */
public final class ScrollSounds {

    private long last = Long.MIN_VALUE;

    public void play(int before, int after) {
        float pitch = moved(before, after, System.nanoTime() / 1000000);
        if (pitch > 0 && Config.extraScreenScroll) ExtraSounds.ui(ExtraSounds.id("generic.scroll"), pitch);
    }

    public float moved(int before, int after, long now) {
        if (before == after) return 0;
        long elapsed = last == Long.MIN_VALUE ? Long.MAX_VALUE : now - last;
        if (elapsed <= 20) return 0;
        last = now;
        return 0.9F + 0.1F * Math.min(1, 50.0F / elapsed);
    }
}
