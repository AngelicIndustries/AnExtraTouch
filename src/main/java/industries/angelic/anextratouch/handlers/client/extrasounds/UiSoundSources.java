package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.audio.ISound;

/** Transfers the UI marker from Minecraft's thread to the OpenAL sound thread. */
public final class UiSoundSources {

    private static final Set<String> SOURCES = ConcurrentHashMap.newKeySet();

    private UiSoundSources() {}

    public static void register(String source, ISound sound) {
        if (sound instanceof ExtraSounds.UiSound) SOURCES.add(source);
    }

    public static boolean isRelative(String source) {
        return SOURCES.contains(source);
    }

    public static void remove(String source) {
        if (source != null) SOURCES.remove(source);
    }
}
