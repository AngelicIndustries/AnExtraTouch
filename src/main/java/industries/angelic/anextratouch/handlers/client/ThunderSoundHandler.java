package industries.angelic.anextratouch.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import industries.angelic.anextratouch.Config;
import industries.angelic.angellib.util.sound.ICustomMaxDistanceSound;
import industries.angelic.angellib.util.sound.ICustomSourceRadiusSound;
import industries.angelic.angellib.util.sound.SoundUtil;

public final class ThunderSoundHandler {

    private static final ResourceLocation VANILLA_THUNDER = new ResourceLocation("minecraft:ambient.weather.thunder");
    private static final ResourceLocation THUNDER = new ResourceLocation("anextratouch:weather.thunder");

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlaySound(PlaySoundEvent17 event) {
        ISound original = event.result;
        if (!Config.thunderSoundsEnabled || original == null
            || !VANILLA_THUNDER.equals(original.getPositionedSoundLocation())) return;

        event.result = Config.thunderSoundVolume > 0f && original.getVolume() > 0f ? new ThunderSound(original) : null;
    }

    private static final class ThunderSound extends MovingSound
        implements ICustomMaxDistanceSound, ICustomSourceRadiusSound {

        // A source several times wider than the strike is far spreads the rumble around the listener like
        // waterfalls, leaning only slightly toward the strike, whether it is near or far.
        private static final float RADIUS_PER_DISTANCE = 4f;
        private static final float MIN_RADIUS = 16f;

        private final float baseVolume;
        private final float maxDistance;
        private final float sourceRadius;

        private ThunderSound(ISound original) {
            super(THUNDER);
            // Vanilla thunder uses volume 10000 to reach distant players. Separate range from gain so
            // the Weather slider and our volume control remain useful below full volume.
            this.baseVolume = MathHelper.clamp_float(original.getVolume(), 0f, 1f);
            this.maxDistance = SoundUtil.getEffectiveMaxDistance(original, SoundUtil.getVanillaMaxDistance(original));
            this.xPosF = original.getXPosF();
            this.yPosF = original.getYPosF();
            this.zPosF = original.getZPosF();
            EntityLivingBase listener = Minecraft.getMinecraft().renderViewEntity;
            float distance = listener == null ? 0f : (float) listener.getDistance(this.xPosF, this.yPosF, this.zPosF);
            this.sourceRadius = Math.max(MIN_RADIUS, distance * RADIUS_PER_DISTANCE);
            this.field_147663_c = 1f;
            this.field_147666_i = original.getAttenuationType();
            this.repeat = original.canRepeat();
            this.field_147665_h = original.getRepeatDelay();
        }

        @Override
        public float getVolume() {
            return this.baseVolume * Config.thunderSoundVolume;
        }

        @Override
        public float getMaxSoundDistance() {
            return this.maxDistance;
        }

        @Override
        public float getSoundSourceRadius() {
            return this.sourceRadius;
        }

        @Override
        public void update() {
            if (!Config.thunderSoundsEnabled) {
                this.donePlaying = true;
            }
        }
    }
}
