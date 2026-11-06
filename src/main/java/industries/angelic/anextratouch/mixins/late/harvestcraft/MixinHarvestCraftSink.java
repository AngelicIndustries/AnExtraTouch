package industries.angelic.anextratouch.mixins.late.harvestcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

@Pseudo
@Mixin(targets = "com.pam.harvestcraft.BlockPamSink", remap = false)
public abstract class MixinHarvestCraftSink {

    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$water(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        ItemStack held = InteractionCues.copy(player.inventory.getCurrentItem());
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        // The sink fills buckets and bottles and washes the dye out of leather armor, on the server only.
        if (!world.isRemote && held != null && !ItemStack.areItemStacksEqual(held, player.inventory.getCurrentItem())) {
            boolean filled = held.getItem() == Items.bucket || held.getItem() == Items.glass_bottle;
            InteractionCues.send(world, x, y, z, InteractionCues.container(held, filled));
        }
        return handled;
    }
}
