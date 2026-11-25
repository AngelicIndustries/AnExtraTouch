package industries.angelic.anextratouch.mixins.late.OpenBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * Sneak-using an OpenBlocks grave empty-handed equips its owner with the items in it and puts the rest in the
 * inventory, in silence. Digging it up with a shovel already breaks it with a sound.
 */
@Pseudo
@Mixin(targets = "openblocks.common.tileentity.TileEntityGrave", remap = false)
public abstract class MixinGraveRetrieve {

    @Shadow
    public abstract IInventory getInventory();

    @WrapMethod(method = "autoEquipAll")
    private void anextratouch$retrieve(EntityPlayer player, Operation<Void> original) {
        int items = anextratouch$items();
        // The grave removes itself once it is empty, so read where it is first.
        TileEntity grave = (TileEntity) (Object) this;
        int x = grave.xCoord, y = grave.yCoord, z = grave.zCoord;
        original.call(player);
        if (grave.getWorldObj() != null && !grave.getWorldObj().isRemote && anextratouch$items() < items)
            InteractionCues.send(grave.getWorldObj(), x, y, z, InteractionCues.Cue.GRAVE);
    }

    @Unique
    private int anextratouch$items() {
        IInventory inventory = getInventory();
        int items = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++)
            if (inventory.getStackInSlot(slot) != null) items++;
        return items;
    }
}
