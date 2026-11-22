package industries.angelic.anextratouch.mixins.late.mowziesmobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * Planting a Foliaath seed spawns a baby Foliaath, a carnivorous plant creature, in silence. The seed reports every
 * use as done, so the cue follows the plant actually appearing.
 */
@Pseudo
@Mixin(targets = "com.bobmowzie.mowziesmobs.server.item.ItemFoliaathSeed", remap = false)
public abstract class MixinFoliaathSeedPlant {

    @WrapOperation(
        method = { "onItemUse", "func_77648_a" },
        at = @At(
            value = "INVOKE",
            target = "Lcom/bobmowzie/mowziesmobs/server/item/ItemFoliaathSeed;spawnCreature(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLiving;DDD)Lnet/minecraft/entity/Entity;"),
        require = 1)
    private Entity anextratouch$planted(@Coerce Object seed, World world, EntityLiving foliaath, double x, double y,
        double z, Operation<Entity> original) {
        Entity planted = original.call(seed, world, foliaath, x, y, z);
        if (planted != null && !world.isRemote) InteractionCues.send(
            world,
            MathHelper.floor_double(x),
            MathHelper.floor_double(y),
            MathHelper.floor_double(z),
            InteractionCues.Cue.PLANT);
        return planted;
    }
}
