package industries.angelic.anextratouch.mixins.late.HungerOverhaul;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * Hunger Overhaul harvests every BlockCrops on right-click (vanilla wheat, carrots and potatoes, HarvestCraft and
 * Natura crops) in its interaction event, before the crop's own handler runs and finds nothing left to harvest.
 */
@Pseudo
@Mixin(targets = "iguanaman.hungeroverhaul.util.IguanaEventHook", remap = false)
public abstract class MixinHungerOverhaulHarvest {

    @WrapMethod(method = "onPlayerInteraction")
    private void anextratouch$harvest(PlayerInteractEvent event, Operation<Void> original) {
        World world = event.world;
        boolean click = event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && world != null;
        boolean watch = click && !world.isRemote;
        Block block = click ? world.getBlock(event.x, event.y, event.z) : null;
        int meta = click ? world.getBlockMetadata(event.x, event.y, event.z) : 0;
        original.call(event);
        if (click && block instanceof BlockCrops) AnExtraTouch.debug(
            "Hunger Overhaul right-click (" + (world.isRemote ? "client" : "server")
                + ") on "
                + Block.blockRegistry.getNameForObject(block)
                + (event.isCanceled() ? ", event cancelled" : "")
                + " at "
                + event.x
                + ", "
                + event.y
                + ", "
                + event.z
                + ": metadata "
                + meta
                + " -> "
                + world.getBlockMetadata(event.x, event.y, event.z));
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
