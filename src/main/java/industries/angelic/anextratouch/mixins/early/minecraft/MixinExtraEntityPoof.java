package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;

@Mixin(EntityLivingBase.class)
public abstract class MixinExtraEntityPoof {

    @Inject(method = "onDeathUpdate", at = @At("RETURN"))
    private void anextratouch$poof(CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (self.deathTime == 20) ExtraSounds.poof(self);
    }
}
