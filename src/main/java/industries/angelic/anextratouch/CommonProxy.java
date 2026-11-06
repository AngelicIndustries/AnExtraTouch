package industries.angelic.anextratouch;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import industries.angelic.anextratouch.commands.CommandReload;
import industries.angelic.anextratouch.compat.InteractionCues;
import industries.angelic.anextratouch.handlers.server.GrassTramplingHandler;
import industries.angelic.anextratouch.util.MobUtil;

public class CommonProxy {

    public void construct() {}

    public void preInit(FMLPreInitializationEvent event) {
        Config.loadConfig(AnExtraTouch.confFile);
    }

    public void init(FMLInitializationEvent event) {
        GrassTramplingHandler.INSTANCE.initHook();
        FMLCommonHandler.instance()
            .bus()
            .register(new InteractionCues.Ticker());
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (Config.printMobNames) {
            MobUtil.printMobNames();
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandReload());
    }

    public void onConfigReload() {}
}
