package industries.angelic.anextratouch.mixins.late.mowziesmobs;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.ThrowSounds;

/** Mowzie's Mobs' blowgun copies the bow's release when it fires a dart; it puffs instead. */
@Pseudo
@Mixin(targets = "com.bobmowzie.mowziesmobs.server.item.ItemBlowgun", remap = false)
public abstract class MixinBlowgunShot {

    @WrapOperation(
        method = { "onPlayerStoppedUsing", "func_77615_a" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V",
            remap = true),
        remap = false)
    private void anextratouch$shoot(World world, Entity shooter, String sound, float volume, float pitch,
        Operation<Void> original) {
        ThrowSounds.blown(world, shooter, sound, volume, pitch, original);
    }
}
