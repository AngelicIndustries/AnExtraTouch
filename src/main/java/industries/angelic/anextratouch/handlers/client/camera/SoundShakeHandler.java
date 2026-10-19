package industries.angelic.anextratouch.handlers.client.camera;

import net.minecraftforge.client.event.sound.PlaySoundEvent17;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.varinstances.configcaches.SoundShakeCache;
import industries.angelic.anextratouch.varinstances.configcaches.SoundShakeCache.SoundShakeEntry;

public class SoundShakeHandler {

    @SubscribeEvent
    public void onPlaySound(PlaySoundEvent17 event) {
        if (!Config.cameraOverhaulEnabled || !Config.cameraSoundShakesEnabled || event.sound == null) {
            return;
        }

        SoundShakeCache soundShakes = AnExtraTouch.vic != null ? AnExtraTouch.vic.soundShakes : null;
        if (soundShakes == null || !soundShakes.hasSoundShakes()) {
            return;
        }

        // try full resource location first (e.g. minecraft:mob.zombie.step), then path only
        String fullName = event.sound.getPositionedSoundLocation()
            .toString();
        SoundShakeEntry entry = soundShakes.getSoundShake(fullName);
        if (entry == null) {
            entry = soundShakes.getSoundShake(event.name);
        }
        if (entry == null) {
            return;
        }

        float volume = event.sound.getVolume();
        if (volume <= 0f) {
            return;
        }

        ScreenShakeManager.Slot shake = ScreenShakeManager.createDirect();
        shake.trauma = entry.trauma * volume;
        shake.radius = entry.radius;
        shake.frequency = entry.frequency;
        shake.lengthInSeconds = entry.duration;
        shake.position.set(event.sound.getXPosF(), event.sound.getYPosF(), event.sound.getZPosF());
    }
}
