package industries.angelic.anextratouch.core;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@SuppressWarnings("unused")
@LateMixin
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.anextratouch.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return new MixinBuilder().addMixin("MixinDSEnvironStateHandler", MixinBuilder.Side.CLIENT, "dsurround")
            .addMixin("AccessorFavoriteHeart", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinBookmarkHeartbeat", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraBookmarks", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraNeiItems", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraNeiPlacement", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraNeiClone", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraNeiTransfers", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraPanelScroll", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraRecipeScroll", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("MixinExtraScrollContainer", MixinBuilder.Side.CLIENT, "NotEnoughItems")
            .addMixin("AccessorBasicMultiBlockSound", MixinBuilder.Side.CLIENT, "etfuturum")
            .addMixin("AccessorBackpackContainer", MixinBuilder.Side.CLIENT, "adventurebackpack")
            .addMixin("AccessorBackpackScreen", MixinBuilder.Side.CLIENT, "adventurebackpack")
            .addMixin("MixinBackpackActions", MixinBuilder.Side.CLIENT, "adventurebackpack")
            .addMixin("MixinBackpackClicks", MixinBuilder.Side.CLIENT, "adventurebackpack")
            .addMixin("MixinBackpackSync", MixinBuilder.Side.CLIENT, "adventurebackpack")
            .addMixin("MixinBackpackTileSync", MixinBuilder.Side.CLIENT, "adventurebackpack")
            .addMixin("MixinKitchenItems", MixinBuilder.Side.CLIENT, "cookingforblockheads")
            .addMixin("MixinKitchenScroll", MixinBuilder.Side.CLIENT, "cookingforblockheads")
            .addMixin("MixinHarvestCraftCrops", MixinBuilder.Side.BOTH, "harvestcraft")
            .addMixin("MixinHarvestCraftHarvest", MixinBuilder.Side.BOTH, "harvestcraft")
            .addMixin("MixinHarvestCraftSink", MixinBuilder.Side.BOTH, "harvestcraft")
            .addMixin("MixinEnderCoreCropClick", MixinBuilder.Side.BOTH, "endercore")
            .addMixin("MixinHungerOverhaulHarvest", MixinBuilder.Side.BOTH, "HungerOverhaul")
            .addMixin("MixinEvaporatorTank", MixinBuilder.Side.BOTH, "saltymod")
            .addMixin("MixinSaltGrassTill", MixinBuilder.Side.BOTH, "saltymod")
            .addMixin("MixinButterChurn", MixinBuilder.Side.BOTH, "Growthcraft")
            .addMixin("MixinGrowthcraftHarvest", MixinBuilder.Side.BOTH, "Growthcraft")
            .addMixin("MixinGrowthcraftPaddy", MixinBuilder.Side.BOTH, "Growthcraft")
            .addMixin("MixinGrowthcraftTanks", MixinBuilder.Side.BOTH, "Growthcraft")
            .addMixin("MixinBopBucket", MixinBuilder.Side.BOTH, "BiomesOPlenty")
            .addMixin("MixinBopHarvest", MixinBuilder.Side.BOTH, "BiomesOPlenty")
            .addMixin("MixinBopJar", MixinBuilder.Side.BOTH, "BiomesOPlenty")
            .addMixin("MixinBlizzSnowTrail", MixinBuilder.Side.BOTH, "ThermalFoundation")
            .addMixin("MixinThaumicHorizonsBoat", MixinBuilder.Side.BOTH, "ThaumicHorizons")
            .addMixin("MixinExplosionNT", MixinBuilder.Side.BOTH, "hbm")
            .addMixin("MixinExplosionVNT", MixinBuilder.Side.BOTH, "hbm")
            .addMixin("MixinRenderTorex", MixinBuilder.Side.CLIENT, "hbm")
            .build();
    }
}
