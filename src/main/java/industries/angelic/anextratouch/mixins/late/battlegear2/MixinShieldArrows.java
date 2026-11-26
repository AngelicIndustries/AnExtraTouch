package industries.angelic.anextratouch.mixins.late.battlegear2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.compat.InteractionCues;

/** Sneak-right-clicking a Battlegear shield turns arrow catching on or off with only a chat message. */
@Pseudo
@Mixin(targets = "mods.battlegear2.items.ItemShield", remap = false)
public abstract class MixinShieldArrows {

    @WrapMethod(method = { "onItemRightClick", "func_77659_a" })
    private ItemStack anextratouch$toggle(ItemStack shield, World world, EntityPlayer player,
        Operation<ItemStack> original) {
        boolean disabled = anextratouch$disabled(shield);
        ItemStack result = original.call(shield, world, player);
        boolean now = anextratouch$disabled(shield);
        if (!world.isRemote && now != disabled) InteractionCues.send(
            world,
            MathHelper.floor_double(player.posX),
            MathHelper.floor_double(player.posY),
            MathHelper.floor_double(player.posZ),
            now ? InteractionCues.Cue.SHIELD_ARROWS_OFF : InteractionCues.Cue.SHIELD_ARROWS_ON);
        return result;
    }

    private static boolean anextratouch$disabled(ItemStack shield) {
        return shield.getTagCompound() != null && shield.getTagCompound()
            .getBoolean("disabled");
    }
}
