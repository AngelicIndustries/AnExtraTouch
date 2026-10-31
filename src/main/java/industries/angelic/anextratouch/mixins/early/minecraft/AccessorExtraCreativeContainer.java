package industries.angelic.anextratouch.mixins.early.minecraft;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.gui.inventory.GuiContainerCreative$ContainerCreative")
public interface AccessorExtraCreativeContainer {

    @Accessor("itemList")
    List<?> anextratouch$items();
}
