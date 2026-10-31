package industries.angelic.anextratouch.handlers.client.extrasounds;

import industries.angelic.anextratouch.Config;

/** Compares accepted edits; navigation at a boundary and rejected characters stay silent. */
public final class TypingSounds {

    private TypingSounds() {}

    public static void changed(String before, String after, int oldCursor, int cursor, int oldSelection, int selection,
        char character, int key, boolean control) {
        if (!Config.extraTyping || before == null) return;
        String event = action(before, after, oldCursor, cursor, oldSelection, selection, character, key, control);
        if (event != null) ExtraSounds.ui("keyboard." + event);
    }

    public static String action(String before, String after, int oldCursor, int cursor, int oldSelection, int selection,
        char character, int key, boolean control) {
        if (!before.equals(after)) {
            if (character == 24 || control && key == 45) return "cut";
            if (character == 22 || control && key == 47) return "paste";
            if (key == 14 || key == 211) return "erase";
            if (key == 28 || key == 156) return "move";
            return "type";
        }
        return oldCursor != cursor || oldSelection != selection ? "move" : null;
    }
}
