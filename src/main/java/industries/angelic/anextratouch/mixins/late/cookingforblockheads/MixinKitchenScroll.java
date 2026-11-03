package industries.angelic.anextratouch.mixins.late.cookingforblockheads;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import industries.angelic.anextratouch.handlers.client.extrasounds.ScrollSounds;

@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.client.GuiRecipeBook", remap = false)
public abstract class MixinKitchenScroll extends GuiContainer {

    protected MixinKitchenScroll(Container container) {
        super(container);
    }

    @Shadow
    private int currentOffset;

    @Unique
    private final ScrollSounds anextratouch$scroll = new ScrollSounds();

    @WrapMethod(method = { "handleMouseInput", "func_146274_d" })
    private void anextratouch$wheel(Operation<Void> original) {
        int before = currentOffset;
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        original.call();
        // CFB also scrolls behind NEI. Do not add a second cue over an outside panel.
        if (mouseX >= guiLeft && mouseX < guiLeft + xSize && mouseY >= guiTop && mouseY < guiTop + ySize) {
            anextratouch$scroll.play(before, currentOffset);
        }
    }

    @WrapOperation(
        method = { "drawGuiContainerBackgroundLayer", "func_146976_a" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/blay09/mods/cookingforblockheads/client/GuiRecipeBook;setCurrentOffset(I)V",
            ordinal = 1))
    private void anextratouch$drag(@Coerce Object screen, int offset, Operation<Void> original) {
        // The first call only refreshes/clamps the layout; the second is scrollbar dragging.
        int before = currentOffset;
        original.call(screen, offset);
        anextratouch$scroll.play(before, currentOffset);
    }
}
