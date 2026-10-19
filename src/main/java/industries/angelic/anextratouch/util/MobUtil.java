package industries.angelic.anextratouch.util;

import net.minecraft.entity.EntityList;

import industries.angelic.anextratouch.AnExtraTouch;

public class MobUtil {

    public static void printMobNames() {
        AnExtraTouch.LOG.info("=========Mob List=========");
        AnExtraTouch.LOG.info(
            "The printing of this list is for you to know which mob has which class name. You can disable this print in the configs.");
        AnExtraTouch.LOG.info("Player (Player)");
        for (Object e : EntityList.stringToClassMapping.keySet()) {
            AnExtraTouch.LOG.info("{} ({})", e, EntityList.stringToClassMapping.get(e));
        }
        AnExtraTouch.LOG.info("=============================");
    }
}
