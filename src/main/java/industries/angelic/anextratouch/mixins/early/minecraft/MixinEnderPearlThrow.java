package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.ThrowSounds;

/**
 * Ender pearls throw instead of a low bow release. BugTorch overwrites the throw at the default priority to allow it in
 * creative mode, keeping the same sound call; Mixin only injects into an overwritten method from a higher priority.
 */
@Mixin(value = ItemEnderPearl.class, priority = 1100)
public abstract class MixinEnderPearlThrow {

    @WrapOperation(
        method = "onItemRightClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"))
    private void anextratouch$throw(World world, Entity thrower, String sound, float volume, float pitch,
        Operation<Void> original) {
        ThrowSounds.thrown(world, thrower, sound, volume, pitch, original);
    }
}
