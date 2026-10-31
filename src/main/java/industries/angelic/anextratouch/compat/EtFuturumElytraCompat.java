package industries.angelic.anextratouch.compat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

@SideOnly(Side.CLIENT)
public final class EtFuturumElytraCompat {

    private static final String MOD_ID = "etfuturum";

    private static Boolean available;

    private EtFuturumElytraCompat() {}

    public static boolean isElytraFlying(Entity entity) {
        return isAvailable() && Bridge.isElytraFlying(entity);
    }

    public static ItemStack getBaubleElytra(EntityLivingBase entity) {
        return isAvailable() ? Bridge.getBaubleElytra(entity) : null;
    }

    private static boolean isAvailable() {
        if (available == null) {
            available = Loader.isModLoaded(MOD_ID);
        }
        return available;
    }

    private static final class Bridge {

        private Bridge() {}

        private static boolean isElytraFlying(Entity entity) {
            return entity instanceof IElytraPlayer && ((IElytraPlayer) entity).etfu$isElytraFlying();
        }

        private static ItemStack getBaubleElytra(EntityLivingBase entity) {
            ItemStack elytra = ItemArmorElytra.getElytra(entity);
            // The chest slot is already tracked with vanilla armor.
            return elytra == entity.getEquipmentInSlot(3) ? null : elytra;
        }
    }
}
