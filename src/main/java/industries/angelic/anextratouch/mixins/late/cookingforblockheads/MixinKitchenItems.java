package industries.angelic.anextratouch.mixins.late.cookingforblockheads;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.KitchenItemSounds;

@Pseudo
@Mixin(
    targets = { "net.blay09.mods.cookingforblockheads.block.BlockToolRack",
        "net.blay09.mods.cookingforblockheads.block.BlockOven",
        "net.blay09.mods.cookingforblockheads.block.BlockToaster" },
    remap = false)
public abstract class MixinKitchenItems {

    // Both names are explicit because CFB is optional and not on the compile classpath.
    @WrapMethod(method = { "onBlockActivated", "func_149727_a" })
    private boolean anextratouch$interact(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ, Operation<Boolean> original) {
        KitchenItemSounds sounds = null;
        if (world.isRemote && player == Minecraft.getMinecraft().thePlayer && Config.extraInventoryClicks) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof IInventory) sounds = new KitchenItemSounds((IInventory) tile);
        }
        boolean handled = original.call(world, x, y, z, player, side, hitX, hitY, hitZ);
        // The toaster returns false even after inserting bread. Compare actual contents.
        if (sounds != null) sounds.finish();
        return handled;
    }
}
