package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.effects.ChestBubbleManager;

@Mixin(BlockChest.class)
public class MixinBlockChest {

    @Inject(method = "onBlockActivated", at = @At("HEAD"))
    private void anextratouch$releaseBubbles(World world, int x, int y, int z, EntityPlayer player, int side,
        float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.chestBubblesEnabled || !world.isRemote) {
            return;
        }

        BlockChest chest = (BlockChest) (Object) this;
        if (ChestBubbleManager.canSpawnOpenBubbles(world, x, y, z, chest)) {
            ChestBubbleManager.spawnOpenBubbles(world, x, y, z);
        }
    }
}
