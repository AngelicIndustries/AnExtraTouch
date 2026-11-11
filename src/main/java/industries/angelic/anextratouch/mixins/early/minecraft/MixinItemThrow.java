package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.ThrowSounds;

/** Splash potions, snowballs, eggs and bottles o' enchanting throw instead of a low bow release. */
@Mixin({ ItemPotion.class, ItemSnowball.class, ItemEgg.class, ItemExpBottle.class })
public abstract class MixinItemThrow {

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
