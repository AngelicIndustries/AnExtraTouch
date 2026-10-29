package industries.angelic.anextratouch;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import industries.angelic.anextratouch.handlers.client.ClientHandler;
import industries.angelic.anextratouch.handlers.client.ItemSoundHandler;
import industries.angelic.anextratouch.handlers.client.SmoothGuiHandler;
import industries.angelic.anextratouch.handlers.client.StepSoundHandler;
import industries.angelic.anextratouch.handlers.client.ThunderSoundHandler;
import industries.angelic.anextratouch.handlers.client.camera.DecoupledCameraHandler;
import industries.angelic.anextratouch.handlers.client.camera.SoundShakeHandler;
import industries.angelic.anextratouch.handlers.client.effects.BreathHandler;
import industries.angelic.anextratouch.handlers.client.effects.ThermalEffectsManager;
import industries.angelic.anextratouch.handlers.client.effects.WakeTrailManager;
import industries.angelic.anextratouch.handlers.client.effects.WaterCascadeManager;
import industries.angelic.anextratouch.handlers.client.effects.WaterRippleManager;
import industries.angelic.anextratouch.handlers.client.effects.WaterSplashManager;
import industries.angelic.anextratouch.handlers.client.effects.WaterWaveManager;
import industries.angelic.anextratouch.handlers.client.effects.WetParticleHandler;
import industries.angelic.anextratouch.varinstances.VarInstanceClient;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        AnExtraTouch.vic = new VarInstanceClient();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        FMLCommonHandler.instance()
            .bus()
            .register(new StepSoundHandler());
        MinecraftForge.EVENT_BUS.register(new SmoothGuiHandler());
        BreathHandler breathHandler = new BreathHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(breathHandler);
        MinecraftForge.EVENT_BUS.register(breathHandler);
        MinecraftForge.EVENT_BUS.register(new WetParticleHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(WaterSplashManager.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(WaterRippleManager.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(WakeTrailManager.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(WaterWaveManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WaterWaveManager.INSTANCE);
        FMLCommonHandler.instance()
            .bus()
            .register(WaterCascadeManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WaterCascadeManager.INSTANCE);
        ThermalEffectsManager.INSTANCE.onConfigReload();
        FMLCommonHandler.instance()
            .bus()
            .register(ThermalEffectsManager.INSTANCE);
        ClientHandler clientHandler = new ClientHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(clientHandler);
        MinecraftForge.EVENT_BUS.register(clientHandler);
        MinecraftForge.EVENT_BUS.register(new SoundShakeHandler());
        MinecraftForge.EVENT_BUS.register(new ThunderSoundHandler());
        ItemSoundHandler.INSTANCE.onConfigReload();
        FMLCommonHandler.instance()
            .bus()
            .register(ItemSoundHandler.INSTANCE);
        DecoupledCameraHandler.registerKeybinding();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        AnExtraTouch.vic.postInitHook();
    }

    @Override
    public void onConfigReload() {
        super.onConfigReload();
        AnExtraTouch.vic.populateListsFromConfig();
        WaterCascadeManager.INSTANCE.onConfigReload();
        WaterWaveManager.INSTANCE.onConfigReload();
        ItemSoundHandler.INSTANCE.onConfigReload();
        ThermalEffectsManager.INSTANCE.onConfigReload();
    }

}
