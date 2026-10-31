package industries.angelic.anextratouch.mixins.late.NotEnoughItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import codechicken.nei.drawable.DrawableResource;
import codechicken.nei.recipe.GuiFavoriteButton;

@Mixin(value = GuiFavoriteButton.class, remap = false)
public interface AccessorFavoriteHeart {

    @Accessor("ICON_STATE_ON")
    static DrawableResource anextratouch$heart() {
        throw new AssertionError("Mixin accessor was not applied");
    }
}
