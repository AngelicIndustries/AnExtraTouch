package industries.angelic.anextratouch.mixins.late.cookingforblockheads;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import industries.angelic.anextratouch.compat.InteractionCues;

/**
 * The cooking table crafts and smelts on the server when a recipe is clicked: a craft succeeds when the craft book
 * returns a result, and a smelt when an ingredient goes into an oven. Clicking only previews a recipe otherwise.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.container.ContainerRecipeBook", remap = false)
public abstract class MixinCookingTableCraft {

    @Shadow(remap = false)
    @Final
    private EntityPlayer player;

    @Unique
    private boolean anextratouch$crafted;

    @ModifyExpressionValue(
        method = "tryCraft",
        at = @At(
            value = "INVOKE",
            target = "Lnet/blay09/mods/cookingforblockheads/container/inventory/InventoryCraftBook;craft(Lnet/minecraft/entity/player/EntityPlayer;Lnet/blay09/mods/cookingforblockheads/registry/food/FoodRecipe;)Lnet/minecraft/item/ItemStack;",
            remap = false))
    private ItemStack anextratouch$crafted(ItemStack result) {
        if (result != null) anextratouch$crafted = true;
        return result;
    }

    @Inject(method = "tryCraft", at = @At("RETURN"))
    private void anextratouch$craft(CallbackInfo ci) {
        if (!anextratouch$crafted) return;
        // Shift-crafting repeats until the stack is full; it sounds once.
        anextratouch$crafted = false;
        cue(InteractionCues.Cue.COOK_CRAFT);
    }

    @Inject(
        method = "trySmelt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/blay09/mods/cookingforblockheads/KitchenMultiBlock;smeltItem(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;",
            remap = false))
    private void anextratouch$smelt(CallbackInfo ci) {
        cue(InteractionCues.Cue.COOK_SMELT);
    }

    @Unique
    private void cue(InteractionCues.Cue cue) {
        InteractionCues.send(
            player.worldObj,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ),
            cue);
    }
}
