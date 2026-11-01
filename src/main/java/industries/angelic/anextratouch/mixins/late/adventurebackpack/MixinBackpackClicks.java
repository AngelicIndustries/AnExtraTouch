package industries.angelic.anextratouch.mixins.late.adventurebackpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import com.darkona.adventurebackpack.inventory.ContainerAdventure;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.adventurebackpack.BackpackSounds;

@Mixin(ContainerAdventure.class)
public abstract class MixinBackpackClicks {

    @WrapMethod(method = "slotClick")
    private ItemStack anextratouch$click(int slot, int button, int mode, EntityPlayer player,
        Operation<ItemStack> original) {
        BackpackSounds.Click click = BackpackSounds
            .begin((ContainerAdventure) (Object) this, player, slot, button, mode);
        ItemStack result = original.call(slot, button, mode, player);
        if (click != null) click.finish();
        return result;
    }
}
