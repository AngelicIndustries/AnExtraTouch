package industries.angelic.anextratouch.handlers.client.extrasounds;

import industries.angelic.anextratouch.Config;

/** Page buttons turn a page, as in modern Minecraft, instead of clicking. */
public final class BookPages {

    private BookPages() {}

    public static boolean turn() {
        if (!Config.extraBookPages) return false;
        ExtraSounds.ui("ui.book.page_turn");
        return true;
    }
}
