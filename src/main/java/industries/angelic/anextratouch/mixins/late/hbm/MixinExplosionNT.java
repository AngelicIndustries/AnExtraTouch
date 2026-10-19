package industries.angelic.anextratouch.mixins.late.hbm;

import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.network.NetworkRegistry;
import industries.angelic.anextratouch.network.NetworkHandler;
import industries.angelic.anextratouch.network.message.MessageExplosionShake;

@Pseudo
@Mixin(targets = "com.hbm.explosion.ExplosionNT", remap = false)
public abstract class MixinExplosionNT {

    @Shadow
    private World worldObj;

    @Inject(method = "func_77279_a", at = @At("RETURN"))
    private void anextratouch$onExplosion(CallbackInfo ci) {
        if (worldObj.isRemote) return;

        Explosion explosion = (Explosion) (Object) this;

        double posX = explosion.explosionX;
        double posY = explosion.explosionY;
        double posZ = explosion.explosionZ;

        NetworkHandler.channel.sendToAllAround(
            new MessageExplosionShake(posX, posY, posZ, explosion.explosionSize),
            new NetworkRegistry.TargetPoint(
                worldObj.provider.dimensionId,
                posX,
                posY,
                posZ,
                explosion.explosionSize * 10f));
    }

}
