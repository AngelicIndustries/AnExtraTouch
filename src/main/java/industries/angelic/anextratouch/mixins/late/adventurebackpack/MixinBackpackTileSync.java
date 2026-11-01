package industries.angelic.anextratouch.mixins.late.adventurebackpack;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;

import industries.angelic.anextratouch.compat.adventurebackpack.BackpackSounds;

@Mixin(value = TileAdventureBackpack.class, remap = false)
public abstract class MixinBackpackTileSync {

    @Inject(method = "onDataPacket", at = @At("RETURN"))
    private void anextratouch$sync(NetworkManager net, S35PacketUpdateTileEntity packet, CallbackInfo ci) {
        BackpackSounds.tile((TileAdventureBackpack) (Object) this, packet.func_148857_g());
    }
}
