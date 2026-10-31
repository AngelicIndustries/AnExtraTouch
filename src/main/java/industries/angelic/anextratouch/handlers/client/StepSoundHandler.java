package industries.angelic.anextratouch.handlers.client;

import java.util.WeakHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.compat.EtFuturumElytraCompat;
import industries.angelic.anextratouch.varinstances.configcaches.ArmorCache;

/** Detects armor equip and unequip changes on the client. */
public class StepSoundHandler {

    static class ArmorTracker {

        private final ItemStack[] prevArmor;

        ArmorTracker(ItemStack[] equipment) {
            prevArmor = new ItemStack[equipment.length];
            for (int i = 0; i < equipment.length; i++) {
                prevArmor[i] = equipment[i] == null ? null : equipment[i].copy();
            }
        }

        String update(ItemStack[] equipment, ArmorCache armor) {
            String bestCategory = null;
            int bestPriority = -1;
            for (int i = 0; i < equipment.length; i++) {
                ItemStack current = equipment[i];
                if (isSameArmorItem(prevArmor[i], current)) continue;

                ItemStack relevant = current != null ? current : prevArmor[i];
                if (relevant != null) {
                    String category = armor.resolveArmorCategory(relevant);
                    int priority = armor.getArmorPriority(category);
                    if (priority > bestPriority) {
                        bestPriority = priority;
                        bestCategory = category;
                    }
                }
                prevArmor[i] = current == null ? null : current.copy();
            }
            return bestCategory;
        }
    }

    private final WeakHashMap<EntityLivingBase, ArmorTracker> trackers = new WeakHashMap<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Config.armorSoundsEnabled) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof EntityLivingBase)) continue;

            EntityLivingBase living = (EntityLivingBase) obj;
            if (!AnExtraTouch.vic.armor.armorSoundEntities.contains(living.getClass())) continue;

            ArmorTracker tracker = trackers.get(living);
            if (tracker == null) {
                tracker = new ArmorTracker(equipment(living));
                trackers.put(living, tracker);
            } else {
                checkEquipSound(living, tracker);
            }
        }
    }

    private static ItemStack[] equipment(EntityLivingBase living) {
        ItemStack[] equipment = new ItemStack[5];
        for (int i = 0; i < 4; i++) {
            equipment[i] = living.getEquipmentInSlot(i + 1);
        }
        equipment[4] = EtFuturumElytraCompat.getBaubleElytra(living);
        return equipment;
    }

    private static void checkEquipSound(EntityLivingBase living, ArmorTracker tracker) {
        String bestCategory = tracker.update(equipment(living), AnExtraTouch.vic.armor);
        if (bestCategory != null) {
            playEquipSound(living, bestCategory);
        }
    }

    static boolean isSameArmorItem(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;

        // Charge, modes and other NBT can change every tick without re-equipping the item.
        // Damage is only part of the identity when the item declares metadata subtypes.
        return !a.getHasSubtypes() || a.getItemDamage() == b.getItemDamage();
    }

    private static void playEquipSound(EntityLivingBase entity, String category) {
        String soundName = getEquipSoundName(category);
        if (soundName == null) return;

        Minecraft.getMinecraft().theWorld.playSound(
            entity.posX,
            entity.posY - entity.yOffset,
            entity.posZ,
            soundName,
            Config.armorSoundVolume,
            1.0f,
            false);
    }

    private static String getEquipSoundName(String category) {
        switch (category) {
            case "light":
                return AnExtraTouch.MODID + ":armor.light_walk";
            case "medium":
                return AnExtraTouch.MODID + ":armor.medium_walk";
            case "heavy":
                return AnExtraTouch.MODID + ":armor.heavy_walk";
            case "crystal":
                return AnExtraTouch.MODID + ":armor.crystal_walk";
            case "elytra":
            case "netherite":
            case "turtle":
            case "generic":
                return AnExtraTouch.MODID + ":armor." + category + "_equip";
            default:
                return null;
        }
    }
}
