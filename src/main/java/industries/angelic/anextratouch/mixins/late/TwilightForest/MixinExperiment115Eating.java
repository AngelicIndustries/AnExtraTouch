package industries.angelic.anextratouch.mixins.late.TwilightForest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.effects.CakeEatingEffect;

/** Twilight Forest's Experiment 115 is its own cake block, eaten in silence like vanilla's; it gets the same crumbs. */
@Pseudo
@Mixin(targets = "twilightforest.block.BlockTFExperiment115", remap = false)
public abstract class MixinExperiment115Eating {

    @Inject(method = "eatCake", at = @At("HEAD"), remap = false, require = 1)
    private void anextratouch$eat(World world, int x, int y, int z, EntityPlayer player, CallbackInfo ci) {
        if (!Config.cakeEatingParticlesEnabled || !world.isRemote || !player.canEat(false)) return;
        Item cake = (Item) Item.itemRegistry.getObject("TwilightForest:item.experiment115");
        if (cake != null) CakeEatingEffect.spawn(player, new ItemStack(cake));
    }
}
