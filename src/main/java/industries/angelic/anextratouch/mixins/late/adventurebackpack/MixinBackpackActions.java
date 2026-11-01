package industries.angelic.anextratouch.mixins.late.adventurebackpack;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.util.Wearing;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;

@Mixin(value = ServerActions.class, remap = false)
public abstract class MixinBackpackActions {

    @WrapMethod(method = "switchHose")
    private static void anextratouch$hose(EntityPlayer player, boolean up, boolean tank, Operation<Void> original) {
        boolean local = player == Minecraft.getMinecraft().thePlayer && Config.extraScreenScroll
            && Wearing.isHoldingHose(player);
        ItemStack held = player.getHeldItem();
        int before = local ? tank ? ItemHose.getHoseTank(held) : ItemHose.getHoseMode(held) : -1;
        original.call(player, up, tank);
        if (local && before != (tank ? ItemHose.getHoseTank(held) : ItemHose.getHoseMode(held))) {
            ExtraSounds.ui(tank ? "backpack.hose.tank" : "backpack.hose.mode");
        }
    }

    @WrapMethod(method = "toggleToolCycling")
    private static void anextratouch$cycling(EntityPlayer player, ItemStack stack, Operation<Void> original) {
        boolean local = player == Minecraft.getMinecraft().thePlayer && Config.extraInventoryClicks;
        boolean before = local && new InventoryBackpack(stack).getDisableCycling();
        original.call(player, stack);
        if (local) {
            boolean after = new InventoryBackpack(stack).getDisableCycling();
            if (before != after) ExtraSounds.ui(after ? "backpack.cycling.off" : "backpack.cycling.on");
        }
    }
}
