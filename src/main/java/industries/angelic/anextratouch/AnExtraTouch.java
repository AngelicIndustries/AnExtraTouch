package industries.angelic.anextratouch;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import industries.angelic.anextratouch.network.NetworkHandler;
import industries.angelic.anextratouch.varinstances.VarInstanceClient;

@Mod(
    modid = AnExtraTouch.MODID,
    version = Tags.VERSION,
    name = "An Extra Touch",
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*",
    dependencies = "required-after:unimixins;required-after:gtnhlib;required-after:angellib@[1.0.0,)",
    guiFactory = "industries.angelic.anextratouch.gui.GuiFactory",
    customProperties = { @Mod.CustomProperty(k = "license", v = "LGPLv3"),
        @Mod.CustomProperty(k = "issueTrackerUrl", v = "https://github.com/AngelicIndustries/AnExtraTouch/issues"),
        @Mod.CustomProperty(k = "iconFile", v = "assets/anextratouch/logo.png"),
        @Mod.CustomProperty(k = "backgroundFile", v = "assets/anextratouch/background.png") })
public class AnExtraTouch {

    public static final String MODID = "anextratouch";
    public static final String MODGROUP = "industries.angelic";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static boolean DEBUG_MODE;
    public static File confFile;
    public static VarInstanceClient vic;

    @SidedProxy(
        clientSide = MODGROUP + "." + MODID + ".ClientProxy",
        serverSide = MODGROUP + "." + MODID + ".CommonProxy")
    public static industries.angelic.anextratouch.CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        String debugVar = System.getenv("MCMODDING_DEBUG_MODE");
        DEBUG_MODE = debugVar != null;
        AnExtraTouch.LOG.info("Debugmode: {}", DEBUG_MODE);
        confFile = event.getSuggestedConfigurationFile();

        NetworkHandler.init();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    public static void debug(String message) {
        if (DEBUG_MODE || Config.debugMode) {
            LOG.info("DEBUG: {}", message);
        }
    }

    public static boolean isDebug() {
        return DEBUG_MODE || Config.debugMode;
    }

    // MAYBEDO
    // Make blazes melt shit below
    // Make blizzes freeze shit below

    // Spawn skeleton with chainmail armor
    // /summon Skeleton ~ ~ ~ {Equipment:[{},{},{},{id:303},{id:302}]}
}
