package industries.angelic.anextratouch.mixins.late.adventurebackpack;

import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.darkona.adventurebackpack.proxy.ClientProxy;

import industries.angelic.anextratouch.compat.adventurebackpack.BackpackSounds;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class MixinBackpackSync {

    @Inject(method = "synchronizePlayer", at = @At("RETURN"))
    private void anextratouch$sync(int id, NBTTagCompound properties, CallbackInfo ci) {
        BackpackSounds.wearable(id, properties);
    }
}
