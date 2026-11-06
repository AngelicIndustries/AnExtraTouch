package industries.angelic.anextratouch.mixins.late.cookingforblockheads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.FluidTanks;
import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * The sink fills empty containers, empties full ones into its tank, rinses items by its sink recipes and otherwise runs
 * its tap, for an empty hand or any item it has no use for, all in silence. Dye only recolours it.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.block.BlockSink", remap = false)
public abstract class MixinKitchenSink {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$water(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(player.inventory.getCurrentItem());
        TileEntity sink = world.getTileEntity(x, y, z);
        int water = FluidTanks.total(FluidTanks.contents(sink));
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        if (world.isRemote) return handled;
        InteractionCues.Cue cue = null;
        if (!handled) {
            // Only the tap declines the click; it runs with a full bucket's worth when the sink requires water.
            if (water >= 1000 || !FluidTanks.sinkRequiresWater()) cue = InteractionCues.Cue.SINK_RINSE;
        } else if (held != null && !dye(held)) {
            boolean changed = !ItemStack.areItemStacksEqual(held, player.inventory.getCurrentItem());
            if (FluidContainerRegistry.isEmptyContainer(held)) {
                if (changed) cue = InteractionCues.container(held, true);
            } else if (FluidContainerRegistry.isFilledContainer(held)) {
                if (changed) cue = InteractionCues.container(held, false);
            } else {
                // A sink recipe rinses even when its result matches, like white wool or undyed armor.
                cue = InteractionCues.Cue.SINK_RINSE;
            }
        }
        InteractionCues.send(world, x, y, z, cue);
        return handled;
    }

    private static boolean dye(ItemStack stack) {
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (OreDictionary.getOreName(id)
                .startsWith("dye")) return true;
        }
        return false;
    }
}
