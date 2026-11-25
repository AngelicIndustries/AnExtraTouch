package industries.angelic.anextratouch.mixins.late.projectzulu;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Right-clicking a Project Zulu tombstone empty-handed hands back the items and experience in it, in silence. */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.blocks.tombstone.TileEntityTombstone", remap = false)
public abstract class MixinZuluTombstoneRetrieve {

    @Shadow
    public int experience;

    @Shadow
    public abstract int getSizeInventory();

    @WrapMethod(method = "giveItemsToPlayer")
    private void anextratouch$retrieve(EntityPlayer player, Operation<Void> original) {
        int items = getSizeInventory();
        int xp = experience;
        original.call(player);
        // Items stay behind when the inventory is full; only a retrieval that moved something sounds.
        TileEntity tombstone = (TileEntity) (Object) this;
        if (tombstone.getWorldObj() != null && !tombstone.getWorldObj().isRemote
            && (getSizeInventory() < items || xp > 0))
            InteractionCues.send(
                tombstone.getWorldObj(),
                tombstone.xCoord,
                tombstone.yCoord,
                tombstone.zCoord,
                InteractionCues.Cue.GRAVE);
    }
}
