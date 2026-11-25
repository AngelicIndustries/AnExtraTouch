package industries.angelic.anextratouch.mixins.late.projectzulu;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.ThrowSounds;

/** Project Zulu's own splash potions are thrown with vanilla's low bow release too; they throw like vanilla's. */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.potion.subitem.SubItemPotion", remap = false)
public abstract class MixinZuluPotionThrow {

    // Project Zulu's own method, so not obfuscated.
    @WrapOperation(
        method = "onItemRightClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V",
            remap = true),
        remap = false)
    private void anextratouch$throw(World world, Entity thrower, String sound, float volume, float pitch,
        Operation<Void> original) {
        ThrowSounds.thrown(world, thrower, sound, volume, pitch, original);
    }
}
