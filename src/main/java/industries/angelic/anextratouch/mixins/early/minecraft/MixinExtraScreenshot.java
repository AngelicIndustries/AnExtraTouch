package industries.angelic.anextratouch.mixins.early.minecraft;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;

@Mixin(ScreenShotHelper.class)
public abstract class MixinExtraScreenshot {

    @Inject(
        method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;",
        at = @At("RETURN"))
    private static void anextratouch$saved(File dir, String name, int width, int height, Framebuffer buffer,
        CallbackInfoReturnable<IChatComponent> cir) {
        IChatComponent message = cir.getReturnValue();
        if (message instanceof ChatComponentTranslation
            && "screenshot.success".equals(((ChatComponentTranslation) message).getKey())) {
            Minecraft.getMinecraft()
                .func_152344_a(() -> { if (Config.extraScreenshots) ExtraSounds.ui("keyboard.screenshot"); });
        }
    }
}
