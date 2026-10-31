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
            .addMixin("MixinBlizzSnowTrail", MixinBuilder.Side.BOTH, "ThermalFoundation")
            .addMixin("MixinThaumicHorizonsBoat", MixinBuilder.Side.BOTH, "ThaumicHorizons")
            .addMixin("MixinExplosionNT", MixinBuilder.Side.BOTH, "hbm")
            .addMixin("MixinExplosionVNT", MixinBuilder.Side.BOTH, "hbm")
            .addMixin("MixinRenderTorex", MixinBuilder.Side.CLIENT, "hbm")
            .build();
    }
}
