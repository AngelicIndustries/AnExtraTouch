package industries.angelic.anextratouch.mixins.late.TwilightForest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Sneak-using a firefly, cicada or moonworm puts it on an empty head, in silence. */
@Pseudo
@Mixin(targets = "twilightforest.block.BlockTFCritter", remap = false)
public abstract class MixinCritterPickup {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$wear(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        ItemStack head = player.inventory.armorInventory[3];
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        if (!world.isRemote && head == null && player.inventory.armorInventory[3] != null)
            InteractionCues.send(world, x, y, z, InteractionCues.Cue.CRITTER);
        return handled;
    }
}
