package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.RecipePageManager;
import industries.angelic.anextratouch.handlers.client.extrasounds.ScrollSounds;

@Mixin(value = GuiRecipe.class, remap = false)
public abstract class MixinExtraRecipeScroll {

    @Shadow
    public int recipetype;

    @Shadow
    private RecipePageManager handlerPages;

    @Unique
    private final ScrollSounds anextratouch$scroll = new ScrollSounds();

    @WrapMethod(method = "mouseScrolled")
    private void anextratouch$mouseScrolled(int direction, Operation<Void> original) {
        int beforeType = recipetype;
        int beforePage = handlerPages.getCurrentPageIndex();
        original.call(direction);
        if (beforeType != recipetype || beforePage != handlerPages.getCurrentPageIndex()) {
            anextratouch$scroll.play(0, 1);
        }
    }
}
