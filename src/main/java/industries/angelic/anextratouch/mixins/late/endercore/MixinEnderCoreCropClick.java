package industries.angelic.anextratouch.mixins.late.endercore;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * EnderCore harvests the crops in its crop config (vanilla wheat, carrots, potatoes and nether wart by default) on an
 * empty-hand right-click, on the server only, and cancels the event before Hunger Overhaul sees it. A sickle can
 * harvest a whole area; one cue plays for the clicked crop.
 */
@Pseudo
@Mixin(targets = "com.enderio.core.common.handlers.RightClickCropHandler", remap = false)
public abstract class MixinEnderCoreCropClick {

    @WrapMethod(method = "handleCropRightClick")
    private void anextratouch$harvest(PlayerInteractEvent event, Operation<Void> original) {
        World world = event.world;
        boolean watch = event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && world != null
            && !world.isRemote;
        Block block = watch ? world.getBlock(event.x, event.y, event.z) : null;
        int meta = watch ? world.getBlockMetadata(event.x, event.y, event.z) : 0;
        original.call(event);
        if (watch && InteractionCues.changed(
            block,
            meta,
            world.getBlock(event.x, event.y, event.z),
            world.getBlockMetadata(event.x, event.y, event.z)))
            InteractionCues.send(
                world,
                event.x,
                event.y,
                event.z,
                InteractionCues.crop(Block.blockRegistry.getNameForObject(block), meta));
    }
}
