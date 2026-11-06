package industries.angelic.anextratouch.mixins.late.cookingforblockheads;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.handlers.client.extrasounds.KitchenDoors;

@Pseudo
@Mixin(
    targets = { "net.blay09.mods.cookingforblockheads.tile.BaseKitchenTileWithInventory",
        "net.blay09.mods.cookingforblockheads.tile.TileOven" },
    remap = false)
public abstract class MixinKitchenDoors {

    @Shadow(remap = false)
    private float prevDoorAngle;

    @Shadow(remap = false)
    private float doorAngle;

    @Inject(method = { "updateEntity", "func_145845_h" }, at = @At("RETURN"))
    private void anextratouch$door(CallbackInfo ci) {
        KitchenDoors.ticked((TileEntity) (Object) this, prevDoorAngle, doorAngle);
    }
}
