package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.entity.effect.EntityLightningBolt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.camera.ScreenShakeManager;

@Mixin(EntityLightningBolt.class)
public abstract class MixinEntityLightningBolt {

    @Shadow
    private int lightningState;

    @Unique
    private boolean anextratouch$shakeCreated;

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void anextratouch$onUpdate(CallbackInfo ci) {
        if (!Config.cameraOverhaulEnabled) return;
        EntityLightningBolt self = (EntityLightningBolt) (Object) this;
        if (!self.worldObj.isRemote) return;
        if (anextratouch$shakeCreated) return;
        // lightningState starts at 2 on the first tick
        if (lightningState != 2) return;

        anextratouch$shakeCreated = true;

        // Local explosion shake
        ScreenShakeManager.Slot explosion = ScreenShakeManager.createDirect();
        explosion.position.set(self.posX, self.posY, self.posZ);
        explosion.radius = 16f;
        explosion.trauma = Config.cameraExplosionTrauma;
        explosion.lengthInSeconds = 3f;

        // distant thunder shake
        ScreenShakeManager.Slot thunder = ScreenShakeManager.createDirect();
        thunder.position.set(self.posX, self.posY, self.posZ);
        thunder.radius = 192f;
        thunder.trauma = Config.cameraThunderTrauma;
        thunder.frequency = 0.5f;
        thunder.lengthInSeconds = 7f;
    }
}
