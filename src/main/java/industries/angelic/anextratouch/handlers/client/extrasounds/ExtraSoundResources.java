package industries.angelic.anextratouch.handlers.client.extrasounds;

import java.io.IOException;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

/** 1.7.10 drops alias volume/pitch and misreads qualified event names. Apply those for AET pack entries. */
public final class ExtraSoundResources {

    private ExtraSoundResources() {}

    public static boolean load(SoundHandler handler, SoundRegistry registry, ResourceLocation event, SoundList list) {
        if (!"anextratouch".equals(event.getResourceDomain())) return false;
        boolean aliases = false;
        for (Object value : list.getSoundList())
            aliases |= ((SoundList.SoundEntry) value).getSoundEntryType() == SoundList.SoundEntry.Type.SOUND_EVENT;
        if (!aliases) return false;
        SoundEventAccessorComposite definition = handler.getSound(event);
        if (definition == null || list.canReplaceExisting()) {
            definition = new SoundEventAccessorComposite(event, 1, 1, list.getSoundCategory());
            registry.registerSound(definition);
        }
        for (Object value : list.getSoundList()) {
            SoundList.SoundEntry entry = (SoundList.SoundEntry) value;
            String name = entry.getSoundEntryName();
            ResourceLocation target = name.contains(":") ? new ResourceLocation(name)
                : new ResourceLocation(event.getResourceDomain(), name);
            if (entry.getSoundEntryType() == SoundList.SoundEntry.Type.SOUND_EVENT) {
                definition.addSoundToEventPool(
                    new Alias(
                        handler,
                        target,
                        entry.getSoundEntryVolume(),
                        entry.getSoundEntryPitch(),
                        entry.getSoundEntryWeight()));
            } else {
                ResourceLocation file = new ResourceLocation(
                    target.getResourceDomain(),
                    "sounds/" + target.getResourcePath() + ".ogg");
                try {
                    IResource resource = Minecraft.getMinecraft()
                        .getResourceManager()
                        .getResource(file);
                    resource.getInputStream()
                        .close();
                    SoundPoolEntry sample = new SoundPoolEntry(
                        file,
                        entry.getSoundEntryPitch(),
                        entry.getSoundEntryVolume(),
                        entry.isStreaming());
                    definition.addSoundToEventPool(new ISoundEventAccessor() {

                        @Override
                        public int func_148721_a() {
                            return entry.getSoundEntryWeight();
                        }

                        @Override
                        public SoundPoolEntry func_148720_g() {
                            return new SoundPoolEntry(sample);
                        }
                    });
                } catch (IOException missing) {
                    industries.angelic.anextratouch.AnExtraTouch.LOG.warn("Missing sound file {} for {}", file, event);
                }
            }
        }
        return true;
    }

    public static final class Alias implements ISoundEventAccessor {

        private final Function<ResourceLocation, SoundEventAccessorComposite> lookup;
        private final ResourceLocation target;
        private final float volume, pitch;
        private final int weight;
        private boolean resolving;

        public Alias(SoundHandler handler, ResourceLocation target, float volume, float pitch, int weight) {
            this(
                id -> ModernSounds.lookup(handler::getSound, id, ModernSounds.efrNamespace()),
                target,
                volume,
                pitch,
                weight);
        }

        Alias(Function<ResourceLocation, SoundEventAccessorComposite> lookup, ResourceLocation target, float volume,
            float pitch, int weight) {
            this.lookup = lookup;
            this.target = target;
            this.volume = volume;
            this.pitch = pitch;
            this.weight = weight;
        }

        @Override
        public int func_148721_a() {
            if (resolving) return 0;
            resolving = true;
            try {
                SoundEventAccessorComposite event = lookup.apply(target);
                return event == null ? 0 : event.func_148721_a() * weight;
            } finally {
                resolving = false;
            }
        }

        @Override
        public SoundPoolEntry func_148720_g() {
            if (resolving) return SoundHandler.missing_sound;
            resolving = true;
            try {
                SoundEventAccessorComposite event = lookup.apply(target);
                if (event == null) return SoundHandler.missing_sound;
                SoundPoolEntry entry = new SoundPoolEntry(event.func_148720_g());
                entry.setVolume(entry.getVolume() * volume);
                entry.setPitch(entry.getPitch() * pitch);
                return entry;
            } finally {
                resolving = false;
            }
        }
    }
}
