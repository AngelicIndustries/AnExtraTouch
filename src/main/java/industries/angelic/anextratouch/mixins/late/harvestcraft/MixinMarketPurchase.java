package industries.angelic.anextratouch.mixins.late.harvestcraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** A market purchase spawns the bought item at the buyer once the server takes the emeralds. */
@Pseudo
@Mixin(targets = "com.pam.harvestcraft.MessageMarketBuy", remap = false)
public abstract class MixinMarketPurchase {

    @WrapOperation(
        method = "onMessage(Lcom/pam/harvestcraft/MessageMarketBuy;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z",
            remap = true))
    private boolean anextratouch$purchase(World world, Entity item, Operation<Boolean> original) {
        boolean spawned = original.call(world, item);
        if (spawned) InteractionCues.send(
            world,
            MathHelper.floor_double(item.posX),
            MathHelper.floor_double(item.posY),
            MathHelper.floor_double(item.posZ),
            InteractionCues.Cue.PURCHASE);
        return spawned;
    }
}
