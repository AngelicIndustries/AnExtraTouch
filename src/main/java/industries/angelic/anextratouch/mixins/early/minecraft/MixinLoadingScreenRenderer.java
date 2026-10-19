package industries.angelic.anextratouch.mixins.early.minecraft;

import net.minecraft.client.LoadingScreenRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;

@Mixin(LoadingScreenRenderer.class)
public class MixinLoadingScreenRenderer {

    @ModifyConstant(method = "resetProgresAndWorkingMessage", constant = @Constant(intValue = -1))
    private int resetProgresAndWorkingMessage(int constant) {
        return Config.loadingProgressBarEnabled ? AnExtraTouch.vic.chunkLoadingProgress : -1;
    }

}
