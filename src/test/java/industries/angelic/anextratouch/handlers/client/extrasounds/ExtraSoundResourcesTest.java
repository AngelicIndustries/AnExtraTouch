package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;

import org.junit.Test;

public class ExtraSoundResourcesTest {

    @Test
    public void aliasesKeepNamespacesAndMultiplyPitchAndVolumeWithoutMutatingSamples() {
        ResourceLocation target = new ResourceLocation("minecraft:random.click");
        SoundPoolEntry sample = new SoundPoolEntry(
            new ResourceLocation("minecraft:sounds/random/click.ogg"),
            1.5,
            0.8,
            false);
        SoundEventAccessorComposite event = new SoundEventAccessorComposite(target, 1, 1, SoundCategory.PLAYERS);
        event.addSoundToEventPool(new ISoundEventAccessor() {

            public int func_148721_a() {
                return 1;
            }

            public SoundPoolEntry func_148720_g() {
                return new SoundPoolEntry(sample);
            }
        });
        ExtraSoundResources.Alias alias = new ExtraSoundResources.Alias(
            id -> target.equals(id) ? event : null,
            target,
            0.5F,
            1.2F,
            2);
        assertEquals(2, alias.func_148721_a());
        for (int i = 0; i < 3; i++) {
            SoundPoolEntry played = alias.func_148720_g();
            assertEquals(0.4, played.getVolume(), 0.00001);
            assertEquals(1.8, played.getPitch(), 0.00001);
        }
        assertEquals(0.8, sample.getVolume(), 0);
        assertEquals(1.5, sample.getPitch(), 0);
    }

    @Test
    public void circularPackAliasesCannotRecurseForever() {
        Map<ResourceLocation, SoundEventAccessorComposite> events = new HashMap<>();
        ResourceLocation a = ExtraSounds.id("test.a"), b = ExtraSounds.id("test.b");
        events.put(a, new SoundEventAccessorComposite(a, 1, 1, SoundCategory.PLAYERS));
        events.put(b, new SoundEventAccessorComposite(b, 1, 1, SoundCategory.PLAYERS));
        events.get(a)
            .addSoundToEventPool(new ExtraSoundResources.Alias(events::get, b, 1, 1, 1));
        events.get(b)
            .addSoundToEventPool(new ExtraSoundResources.Alias(events::get, a, 1, 1, 1));
        assertEquals(
            0,
            events.get(a)
                .func_148721_a());
    }
}
