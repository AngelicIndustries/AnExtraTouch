package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import codechicken.nei.BookmarkPanel;
import codechicken.nei.PanelWidget;
import codechicken.nei.bookmark.BookmarkGrid;
import codechicken.nei.recipe.Recipe;
import codechicken.nei.recipe.Recipe.RecipeId;
import industries.angelic.anextratouch.handlers.client.extrasounds.BookmarkSounds;

@Mixin(value = BookmarkPanel.class, remap = false)
public abstract class MixinExtraBookmarks extends PanelWidget<BookmarkGrid> {

    @WrapMethod(
        method = { "addItem(Lnet/minecraft/item/ItemStack;Lcodechicken/nei/recipe/Recipe$RecipeId;I)Z", "removeItem" })
    private boolean anextratouch$item(ItemStack stack, RecipeId recipe, int group, Operation<Boolean> original) {
        int before = getGrid().size();
        boolean result = original.call(stack, recipe, group);
        BookmarkSounds.changed(before, getGrid().size());
        return result;
    }

    @WrapMethod(method = "addRecipe(Lcodechicken/nei/recipe/Recipe;II)Z")
    private boolean anextratouch$recipe(Recipe recipe, int multiplier, int group, Operation<Boolean> original) {
        int before = getGrid().size();
        boolean result = original.call(recipe, multiplier, group);
        BookmarkSounds.changed(before, getGrid().size());
        return result;
    }

    @WrapMethod(method = "addGroup")
    private boolean anextratouch$group(List<?> items, BookmarkPanel.BookmarkViewMode mode, boolean crafting,
        Operation<Boolean> original) {
        int before = getGrid().size();
        boolean result = original.call(items, mode, crafting);
        BookmarkSounds.changed(before, getGrid().size());
        return result;
    }

    @WrapMethod(method = "removeSlot")
    private boolean anextratouch$removeSlot(int x, int y, boolean fullRecipe, Operation<Boolean> original) {
        int before = getGrid().size();
        boolean result = original.call(x, y, fullRecipe);
        // A handled click can leave a collapsed recipe unchanged.
        BookmarkSounds.changed(before, getGrid().size());
        return result;
    }

    @WrapMethod(method = "removeRecipe")
    private boolean anextratouch$removeRecipe(RecipeId recipe, int group, Operation<Boolean> original) {
        int before = getGrid().size();
        boolean result = original.call(recipe, group);
        BookmarkSounds.changed(before, getGrid().size());
        return result;
    }

    @WrapMethod(method = "removeGroup")
    private boolean anextratouch$removeGroup(int group, Operation<Boolean> original) {
        int before = getGrid().size();
        boolean result = original.call(group);
        BookmarkSounds.changed(before, getGrid().size());
        return result;
    }
}
