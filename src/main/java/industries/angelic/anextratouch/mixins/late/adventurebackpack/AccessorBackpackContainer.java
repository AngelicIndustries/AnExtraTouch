package industries.angelic.anextratouch.mixins.late.adventurebackpack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.darkona.adventurebackpack.common.Constants.Source;
import com.darkona.adventurebackpack.inventory.ContainerAdventure;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;

@Mixin(value = ContainerAdventure.class, remap = false)
public interface AccessorBackpackContainer {

    @Accessor("inventory")
    IInventoryTanks anextratouch$inventory();

    @Accessor("source")
    Source anextratouch$source();
}
