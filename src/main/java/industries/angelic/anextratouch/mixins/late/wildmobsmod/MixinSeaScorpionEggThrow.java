package industries.angelic.anextratouch.mixins.late.wildmobsmod;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.ThrowSounds;

/** Wild Mobs' sea scorpion egg copies the snowball's low bow release; it throws like the other thrown items. */
@Pseudo
@Mixin(targets = "com.wildmobsmod.items.ItemSeaScorpionEgg", remap = false)
public abstract class MixinSeaScorpionEggThrow {

    @WrapOperation(
        method = { "onItemRightClick", "func_77659_a" },
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
