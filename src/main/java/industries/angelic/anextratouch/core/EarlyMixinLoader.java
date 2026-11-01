package industries.angelic.anextratouch.core;

import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import industries.angelic.anextratouch.AnExtraTouch;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class EarlyMixinLoader extends AngelEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins." + AnExtraTouch.MODID + ".early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new MixinBuilder()
            // Accessors

            .addMixin("MixinExtraInventoryClicks", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraCreativeClicks", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraItemDrop", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraTextField", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraSignTyping", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraBookTyping", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraScreenshot", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraEntityPoof", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraCreativeScroll", MixinBuilder.Side.CLIENT)
            .addMixin("AccessorExtraCreativeContainer", MixinBuilder.Side.CLIENT)
            .addMixin("AccessorExtraSoundHandler", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraSoundResources", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExtraUiSoundSource", MixinBuilder.Side.CLIENT)

            // Rest
            .addMixin("MixinBlockCake", MixinBuilder.Side.CLIENT)
            .addMixin("MixinBlockChest", MixinBuilder.Side.CLIENT)
            .addMixin("MixinBlockEnderChest", MixinBuilder.Side.CLIENT)
            .addMixin("MixinBlockLiquid", MixinBuilder.Side.CLIENT)
            .addMixin("MixinChunk", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityArrow", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityBoat", MixinBuilder.Side.BOTH)
            .addMixin("MixinEntityBubbleFX", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityClient", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityDropParticleFX", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityRainFX", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntitySplashFX", MixinBuilder.Side.CLIENT)
            .addMixin("MixinGuiScreen", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityRenderer", MixinBuilder.Side.CLIENT)
            .addMixin("MixinExplosion", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityLightningBolt", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityLivingBase", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntitySetAngles", MixinBuilder.Side.CLIENT)
            .addMixin("MixinEntityPlayerSP", MixinBuilder.Side.CLIENT)
            .addMixin("MixinSoundHandler", MixinBuilder.Side.CLIENT)
            .addMixin("MixinTileEntityChest", MixinBuilder.Side.CLIENT)
            .addMixin("MixinTileEntityEnderChest", MixinBuilder.Side.CLIENT)
            .addMixin("MixinTextureManager", MixinBuilder.Side.CLIENT)
            .addMixin("MixinLoadingScreenRenderer", MixinBuilder.Side.CLIENT)
            .addMixin("MixinMinecraft", MixinBuilder.Side.CLIENT)
            .addMixin("MixinNetHandlerPlayClient", MixinBuilder.Side.CLIENT)
            .addMixin("MixinMinecraftServer", MixinBuilder.Side.CLIENT)
            .build();

        if (!MixinBuilder.isServer() && loadedCoreMods.contains("com.teamderpy.shouldersurfing.asm.ShoulderPlugin")) {
            mixins.add("shouldersurfing.MixinKeyHandler");
        }
        if (!MixinBuilder.isServer() && loadedCoreMods.contains("com.gtnewhorizons.angelica.loading.AngelicaTweaker")) {
            mixins.add("angelica.MixinDeferredWorldRenderingPipeline");
            mixins.add("angelica.MixinWakeProgramSource");
            mixins.add("angelica.MixinWakeSamplers");
            mixins.add("angelica.MixinWakeUniforms");
        }

        return mixins;
    }
}
