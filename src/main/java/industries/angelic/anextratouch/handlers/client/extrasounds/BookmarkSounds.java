package industries.angelic.anextratouch.handlers.client.extrasounds;

import industries.angelic.anextratouch.Config;

public final class BookmarkSounds {

    private BookmarkSounds() {}

    public static void changed(int before, int after) {
        if (before != after) play(after > before);
    }

    public static void play(boolean added) {
        if (Config.extraBookmarks) ExtraSounds.ui(added ? "bookmark.add" : "bookmark.remove");
    }
}
