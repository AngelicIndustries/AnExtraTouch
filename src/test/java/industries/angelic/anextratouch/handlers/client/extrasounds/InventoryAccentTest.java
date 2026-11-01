package industries.angelic.anextratouch.handlers.client.extrasounds;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;

import org.junit.Test;

public class InventoryAccentTest {

    @Test
    public void inventoryAccentPlaysAlongsideBaseWithTheSameControls() {
        ResourceLocation event = ExtraSounds.id("item.pickup.BiomesOPlenty.jarFilled.2");
        ResourceLocation accent = ExtraSounds.id(event.getResourcePath() + ".accent");
        SoundEventAccessorComposite definition = new SoundEventAccessorComposite(accent, 1, 1, SoundCategory.PLAYERS);
        definition.addSoundToEventPool(new ISoundEventAccessor() {

            public int func_148721_a() {
                return 1;
            }

            public SoundPoolEntry func_148720_g() {
                return null;
            }
        });
        List<ISound> played = new ArrayList<>();
        ExtraSounds.ui(event, 0.9F, 0.6F, id -> accent.equals(id) ? definition : null, played::add);
        assertEquals(2, played.size());
        assertEquals(
            event,
            played.get(0)
                .getPositionedSoundLocation());
        assertEquals(
            accent,
            played.get(1)
                .getPositionedSoundLocation());
        for (ISound sound : played) {
            assertEquals(0.9, sound.getPitch(), 0.00001);
            assertEquals(0.6, sound.getVolume(), 0.00001);
            assertEquals(ISound.AttenuationType.NONE, sound.getAttenuationType());
        }
        played.clear();
        ExtraSounds.ui(event, 1, 0, id -> definition, played::add);
        assertTrue(played.isEmpty());
    }

    @Test
    public void absentAndEmptyAccentsLeaveOnlyTheBaseCue() {
        ResourceLocation event = ExtraSounds.id("item.pickup.BiomesOPlenty.misc.11");
        List<ISound> played = new ArrayList<>();
        ExtraSounds.ui(event, 1, 1, id -> null, played::add);
        assertEquals(1, played.size());
        played.clear();
        ExtraSounds
            .ui(event, 1, 1, id -> new SoundEventAccessorComposite(id, 1, 1, SoundCategory.PLAYERS), played::add);
        assertEquals(1, played.size());
        played.clear();
        ExtraSounds.ui(ExtraSounds.id("inventory.open"), 1, 1, id -> {
            fail("Non-item cue looked for an accent");
            return null;
        }, played::add);
        assertEquals(1, played.size());
    }
}
