package industries.angelic.anextratouch.mixins.late.adventurebackpack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.darkona.adventurebackpack.client.gui.GuiWithTanks;
import com.darkona.adventurebackpack.common.Constants.Source;

@Mixin(value = GuiWithTanks.class, remap = false)
public interface AccessorBackpackScreen {

    @Accessor("source")
    Source anextratouch$source();
}
