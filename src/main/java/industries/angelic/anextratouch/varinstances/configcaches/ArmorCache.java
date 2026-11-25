package industries.angelic.anextratouch.varinstances.configcaches;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;

public class ArmorCache {

    public HashSet<Class<? extends Entity>> armorSoundEntities;
    private final HashMap<String, String> armorCategoryOverrideMap = new HashMap<>();
    private final Map<String, List<String>> armorNameRules = new HashMap<>();
    // Gold and iron share AET's heavy category, so their relative order has no effect.
    private static final String[] NAME_ORDER = { "none", "medium", "crystal", "heavy", "light", "netherite", "elytra",
        "turtle", "generic" };

    private static final String[][] NAME_PHRASES = { {}, { "chain" }, { "diamond" }, { "gold", "iron" }, { "leather" },
        { "netherite" }, { "elytra" }, {}, {} };

    /**
     * Built-in defaults; configured overrides still win. Twilight Forest critters worn on the head play their own
     * pickup
     * cue, and naga scale armor is light, not the default heavy. Mowzie's Mobs' wooden Barakoa masks report chain mail
     * and Mutant Creatures' bone armor iron; they knock like a turtle shell instead. Project Zulu's cloth, fur, cactus
     * and lizard scale armor use custom materials that fell to the default heavy.
     */
    private static final Map<String, String> BUILT_IN_CATEGORIES = new HashMap<>();

    static {
        BUILT_IN_CATEGORIES.put("TwilightForest:item.critter", "none");
        BUILT_IN_CATEGORIES.put("TwilightForest:item.plateNaga", "light");
        BUILT_IN_CATEGORIES.put("TwilightForest:item.legsNaga", "light");
        for (int mask = 1; mask <= 5; mask++) BUILT_IN_CATEGORIES.put("mowziesmobs:barakoaMask" + mask, "turtle");
        for (String piece : new String[] { "Head", "Chest", "Legs", "Boots" })
            BUILT_IN_CATEGORIES.put("MutantCreatures:item.skeleArmor" + piece, "turtle");
        for (String piece : new String[] { "Helmet", "Chest", "Legs", "Boots" }) {
            for (String cloth : new String[] { "Blue", "Green", "Red", "White" })
                BUILT_IN_CATEGORIES.put("ProjectZulu|Core:" + cloth + "Cloth" + piece, "light");
            BUILT_IN_CATEGORIES.put("ProjectZulu|Core:Fur" + piece, "light");
            BUILT_IN_CATEGORIES.put("ProjectZulu|Core:Cactus" + piece, "light");
            BUILT_IN_CATEGORIES.put("ProjectZulu|Core:Scale" + piece, "turtle");
        }
    }

    public void populateFromConfig() {
        armorSoundEntities = new HashSet<>();
        HashSet<String> armorEntityNames = new HashSet<>();
        Collections.addAll(armorEntityNames, Config.armorSoundEntityWhitelist);
        for (Class<? extends Entity> c : EntityList.stringToClassMapping.values()) {
            if (!EntityLivingBase.class.isAssignableFrom(c)) continue;
            String name = EntityList.classToStringMapping.get(c);
            if (armorEntityNames.contains(name)) {
                armorSoundEntities.add(c);
            }
        }
        if (armorEntityNames.contains("Player")) {
            armorSoundEntities.add(EntityClientPlayerMP.class);
            armorSoundEntities.add(EntityOtherPlayerMP.class);
            armorSoundEntities.add(EntityPlayerMP.class);
        }

        populateSoundRules(Config.armorCategoryOverrides, Config.armorNameRules);
    }

    void populateSoundRules(String[] overrides, String[] names) {
        armorCategoryOverrideMap.clear();
        armorNameRules.clear();
        armorCategoryOverrideMap.putAll(BUILT_IN_CATEGORIES);
        for (String entry : overrides) {
            String[] parts = entry.split(";", -1);
            if (parts.length != 2) {
                AnExtraTouch.LOG.error("Invalid armor category override entry: {}", entry);
                continue;
            }
            armorCategoryOverrideMap.put(
                parts[0].trim(),
                parts[1].trim()
                    .toLowerCase(Locale.ROOT));
        }
        for (String entry : names) {
            String[] parts = entry.split(":", -1);
            String category = parts[0].trim()
                .toLowerCase(Locale.ROOT);
            if (parts.length != 2 || parts[1].trim()
                .isEmpty() || !validNameCategory(category)) {
                AnExtraTouch.LOG.error("Invalid armor name rule: {}", entry);
                continue;
            }
            armorNameRules.computeIfAbsent(category, key -> new ArrayList<>())
                .add(
                    parts[1].trim()
                        .toLowerCase(Locale.ROOT));
        }
    }

    private static boolean validNameCategory(String category) {
        for (String valid : NAME_ORDER) if (valid.equals(category)) return true;
        return false;
    }

    public String resolveArmorCategory(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        // Check override map by registry name
        String registryName = net.minecraft.item.Item.itemRegistry.getNameForObject(stack.getItem());
        if (registryName != null && armorCategoryOverrideMap.containsKey(registryName)) {
            return armorCategoryOverrideMap.get(registryName);
        }

        // Map armor material to category
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor.ArmorMaterial mat = ((ItemArmor) stack.getItem()).getArmorMaterial();
            if (mat == ItemArmor.ArmorMaterial.CLOTH) return "light";
            if (mat == ItemArmor.ArmorMaterial.CHAIN) return "medium";
            if (mat == ItemArmor.ArmorMaterial.IRON) return "heavy";
            if (mat == ItemArmor.ArmorMaterial.GOLD) return "heavy";
            if (mat == ItemArmor.ArmorMaterial.DIAMOND) return "crystal";
        }

        String material = stack.getItem() instanceof ItemArmor ? ((ItemArmor) stack.getItem()).getArmorMaterial()
            .name() : "";
        String matched = resolveName(stack.getUnlocalizedName(), material);
        return matched != null ? matched : Config.armorDefaultCategory;
    }

    String resolveName(String unlocalizedName, String materialName) {
        String name = unlocalizedName.toLowerCase(Locale.ROOT);
        String material = materialName.toLowerCase(Locale.ROOT);
        for (int i = 0; i < NAME_ORDER.length; i++) {
            String category = NAME_ORDER[i];
            for (String phrase : NAME_PHRASES[i])
                if (name.contains(phrase) || material.contains(phrase)) return category;
            List<String> phrases = armorNameRules.get(category);
            if (phrases != null) {
                for (String phrase : phrases) if (name.contains(phrase) || material.contains(phrase)) return category;
            }
        }
        return null;
    }

    public int getArmorPriority(String category) {
        if (category == null) return -1;
        switch (category) {
            case "netherite":
            case "heavy":
                return 3;
            case "crystal":
                return 2;
            case "medium":
                return 1;
            case "turtle":
            case "generic":
            case "light":
                return 0;
            case "elytra":
                return 0;
            default:
                return -1;
        }
    }
}
