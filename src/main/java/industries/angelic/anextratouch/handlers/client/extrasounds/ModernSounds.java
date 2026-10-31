package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.Loader;
import industries.angelic.anextratouch.AnExtraTouch;
import makamys.mclib.core.MCLib;
import makamys.mclib.ext.assetdirector.ADConfig;
import makamys.mclib.ext.assetdirector.AssetDirectorAPI;

/** Downloaded modern sounds, shared with EFR, with local defaults if assets are unavailable. */
public final class ModernSounds {

    static final String VERSION = "1.21.10";
    static final String NAMESPACE = "minecraft_" + VERSION;
    private static final Map<ResourceLocation, ResourceLocation> FALLBACKS = readFallbacks();
    private static boolean checked;
    private static String namespace;

    private ModernSounds() {}

    public static void register() {
        MCLib.init();
        AssetDirectorAPI.register(assetConfig());
    }

    static ADConfig assetConfig() {
        ADConfig config = new ADConfig();
        for (ResourceLocation event : FALLBACKS.keySet()) {
            String name = event.getResourcePath();
            config.addSoundEvent(VERSION, name, category(name));
        }
        return config;
    }

    private static String category(String name) {
        // These definitions are shared with EFR's world sounds. AET's aliases have their own player category.
        if (name.startsWith("block.note_block.")) return "record";
        if (name.endsWith(".step") || name.startsWith("entity.cod.")) return "neutral";
        return name.startsWith("block.") ? "block" : "player";
    }

    static String efrNamespace() {
        if (!checked) {
            checked = true;
            if (Loader.isModLoaded("etfuturum")) {
                try {
                    // Do not inline Reference.MCAssetVer from the compile-time EFR version.
                    namespace = (String) Class.forName("ganymedes01.etfuturum.lib.Reference")
                        .getField("MCAssetVer")
                        .get(null);
                } catch (ReflectiveOperationException | LinkageError e) {
                    AnExtraTouch.LOG.warn("Cannot read EFR's sound namespace; using Extra Sounds fallbacks", e);
                }
            }
        }
        return namespace;
    }

    static SoundEventAccessorComposite lookup(Function<ResourceLocation, SoundEventAccessorComposite> sounds,
        ResourceLocation requested, String efr) {
        SoundEventAccessorComposite exact = sounds.apply(requested);
        if (exact != null) return exact;
        ResourceLocation fallback = FALLBACKS.get(requested);
        // Only modern defaults have a fallback; explicit pack aliases retain their meaning.
        if (fallback == null) return null;
        if (efr != null) {
            SoundEventAccessorComposite modern = sounds.apply(new ResourceLocation(efr, requested.getResourcePath()));
            if (modern != null && modern.func_148721_a() > 0) return modern;
        }
        SoundEventAccessorComposite downloaded = sounds
            .apply(new ResourceLocation(NAMESPACE, requested.getResourcePath()));
        if (downloaded != null && downloaded.func_148721_a() > 0) return downloaded;
        return sounds.apply(fallback);
    }

    private static Map<ResourceLocation, ResourceLocation> readFallbacks() {
        Map<ResourceLocation, ResourceLocation> result = new HashMap<>();
        try (InputStream input = ModernSounds.class
            .getResourceAsStream("/assets/anextratouch/extra_sound_fallbacks.json")) {
            if (input == null) throw new IllegalStateException("Missing Extra Sounds fallbacks");
            JsonObject object = new JsonParser().parse(new InputStreamReader(input, StandardCharsets.UTF_8))
                .getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                result.put(
                    new ResourceLocation(entry.getKey()),
                    new ResourceLocation(
                        entry.getValue()
                            .getAsString()));
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot read Extra Sounds fallbacks", e);
        }
        return result;
    }
}
