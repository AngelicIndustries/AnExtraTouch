package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ModernSoundsTest {

    @Test
    public void registersEveryModernDefaultWithoutDownloadingJars() throws Exception {
        JsonObject config = new Gson().toJsonTree(ModernSounds.assetConfig())
            .getAsJsonObject();
        JsonObject version = config.getAsJsonObject("assets")
            .getAsJsonObject(ModernSounds.VERSION);
        assertFalse(
            version.get("jar")
                .getAsBoolean());
        assertEquals(
            0,
            version.getAsJsonArray("objects")
                .size());
        Map<String, String> requested = new HashMap<>();
        for (JsonElement value : version.getAsJsonArray("soundEvents")) {
            JsonObject event = value.getAsJsonObject();
            requested.put(
                "minecraft:" + event.get("name")
                    .getAsString(),
                event.get("category")
                    .getAsString());
        }
        try (InputStreamReader reader = new InputStreamReader(
            getClass().getResourceAsStream("/assets/anextratouch/extra_sound_fallbacks.json"),
            StandardCharsets.UTF_8)) {
            JsonObject fallbacks = new JsonParser().parse(reader)
                .getAsJsonObject();
            assertEquals(
                fallbacks.entrySet()
                    .size(),
                requested.size());
            for (Map.Entry<String, JsonElement> event : fallbacks.entrySet()) {
                assertNotNull(event.getKey(), requested.get(event.getKey()));
            }
        }
        assertEquals("block", requested.get("minecraft:block.chorus_flower.grow"));
        assertEquals("neutral", requested.get("minecraft:block.chain.step"));
        assertEquals("record", requested.get("minecraft:block.note_block.chime"));
        assertEquals("player", requested.get("minecraft:item.armor.equip_elytra"));
    }

    @Test
    public void downloadedSoundsWorkWithoutEfrAndMissingDownloadsFallBack() {
        Map<ResourceLocation, SoundEventAccessorComposite> sounds = new HashMap<>();
        ResourceLocation requested = new ResourceLocation("minecraft:block.conduit.ambient.short");
        SoundEventAccessorComposite fallback = definition("minecraft:note.bassattack", true);
        SoundEventAccessorComposite downloaded = definition(
            ModernSounds.NAMESPACE + ":block.conduit.ambient.short",
            true);
        sounds.put(fallback.getSoundEventLocation(), fallback);
        sounds.put(downloaded.getSoundEventLocation(), downloaded);
        assertSame(downloaded, ModernSounds.lookup(sounds::get, requested, null));
        assertSame(downloaded, ModernSounds.lookup(sounds::get, requested, "minecraft_missing_efr_assets"));
        sounds.put(
            downloaded.getSoundEventLocation(),
            definition(
                downloaded.getSoundEventLocation()
                    .toString(),
                false));
        assertSame(fallback, ModernSounds.lookup(sounds::get, requested, null));
    }

    @Test
    public void usesRunningEfrNamespaceAndFallsBackWhenAbsentOrMissing() {
        Map<ResourceLocation, SoundEventAccessorComposite> sounds = new HashMap<>();
        ResourceLocation requested = new ResourceLocation("minecraft:item.armor.equip_iron");
        SoundEventAccessorComposite fallback = definition("anextratouch:armor.heavy_walk", false);
        sounds.put(fallback.getSoundEventLocation(), fallback);
        assertSame(fallback, ModernSounds.lookup(sounds::get, requested, null));
        assertSame(fallback, ModernSounds.lookup(sounds::get, requested, "minecraft_test_version"));
        SoundEventAccessorComposite modern = definition("minecraft_test_version:item.armor.equip_iron", true);
        sounds.put(modern.getSoundEventLocation(), modern);
        assertSame(modern, ModernSounds.lookup(sounds::get, requested, "minecraft_test_version"));
        assertSame(fallback, ModernSounds.lookup(sounds::get, requested, null));
        SoundEventAccessorComposite empty = definition("minecraft_test_version:item.armor.equip_iron", false);
        sounds.put(empty.getSoundEventLocation(), empty);
        assertSame(fallback, ModernSounds.lookup(sounds::get, requested, "minecraft_test_version"));
    }

    @Test
    public void explicitPackEventIncludingSilenceWinsAndOtherAliasesAreUntouched() {
        Map<ResourceLocation, SoundEventAccessorComposite> sounds = new HashMap<>();
        SoundEventAccessorComposite exact = definition("minecraft:item.armor.equip_iron", false);
        sounds.put(exact.getSoundEventLocation(), exact);
        assertSame(exact, ModernSounds.lookup(sounds::get, exact.getSoundEventLocation(), "minecraft_test_version"));
        assertNull(
            ModernSounds.lookup(sounds::get, new ResourceLocation("othermod:missing"), "minecraft_test_version"));
    }

    @Test
    public void modernEquipCategoriesHaveAudibleFallbacksWithoutEfr() {
        Map<ResourceLocation, SoundEventAccessorComposite> sounds = new HashMap<>();
        SoundEventAccessorComposite light = definition("anextratouch:armor.light_walk", true);
        SoundEventAccessorComposite heavy = definition("anextratouch:armor.heavy_walk", true);
        sounds.put(light.getSoundEventLocation(), light);
        sounds.put(heavy.getSoundEventLocation(), heavy);
        for (String category : new String[] { "elytra", "netherite", "turtle", "generic" }) {
            ResourceLocation requested = new ResourceLocation("minecraft:item.armor.equip_" + category);
            SoundEventAccessorComposite fallback = "netherite".equals(category) ? heavy : light;
            assertSame(fallback, ModernSounds.lookup(sounds::get, requested, null));
            assertSame(fallback, ModernSounds.lookup(sounds::get, requested, "minecraft_missing_assets"));
            SoundEventAccessorComposite modern = definition(
                "minecraft_test_version:item.armor.equip_" + category,
                true);
            sounds.put(modern.getSoundEventLocation(), modern);
            assertSame(modern, ModernSounds.lookup(sounds::get, requested, "minecraft_test_version"));
        }
    }

    private static SoundEventAccessorComposite definition(String name, boolean sample) {
        SoundEventAccessorComposite event = new SoundEventAccessorComposite(
            new ResourceLocation(name),
            1,
            1,
            SoundCategory.PLAYERS);
        if (sample) event.addSoundToEventPool(new ISoundEventAccessor() {

            public int func_148721_a() {
                return 1;
            }

            public SoundPoolEntry func_148720_g() {
                return new SoundPoolEntry(new ResourceLocation("minecraft:sounds/random/click.ogg"), 1, 1, false);
            }
        });
        return event;
    }
}
