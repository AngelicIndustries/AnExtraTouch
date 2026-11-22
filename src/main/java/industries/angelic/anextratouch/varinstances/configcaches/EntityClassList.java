package industries.angelic.anextratouch.varinstances.configcaches;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.EntityList;

/**
 * Entity names from a config list. An entry also covers mobs built on that mob, so modded variants (Special Mobs'
 * blazes, Twilight Forest's ghasts) follow their vanilla entry.
 */
public final class EntityClassList {

    private EntityClassList() {}

    /** The registered classes of the listed names; unknown names, such as missing mods' mobs, are skipped. */
    public static Set<Class<?>> classes(String[] names) {
        Set<Class<?>> classes = new HashSet<>();
        for (String name : names) {
            Class<?> type = (Class<?>) EntityList.stringToClassMapping.get(name);
            if (type != null) classes.add(type);
        }
        return classes;
    }

    public static boolean listed(Class<?> type, Set<Class<?>> listed) {
        for (Class<?> entry : listed) if (entry.isAssignableFrom(type)) return true;
        return false;
    }
}
