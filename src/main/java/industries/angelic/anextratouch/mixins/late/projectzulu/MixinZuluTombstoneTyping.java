package industries.angelic.anextratouch.mixins.late.projectzulu;

import java.lang.reflect.Field;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.extrasounds.TypingSounds;

/**
 * Project Zulu's tombstone screen edits its lines like a sign but with its own screen, so typing was silent. Its
 * tombstone field's type is not on the compile classpath, so the lines are read by reflection.
 */
@Pseudo
@Mixin(targets = "com.stek101.projectzulu.common.blocks.tombstone.GuiTombstone", remap = false)
public abstract class MixinZuluTombstoneTyping {

    @Unique
    private static Field anextratouch$tombstone;
    @Unique
    private static Field anextratouch$lines;
    @Unique
    private static boolean anextratouch$broken;

    @Shadow
    private int editLine;
    @Unique
    private String anextratouch$text;
    @Unique
    private int anextratouch$line;

    @Inject(method = { "keyTyped", "func_73869_a" }, at = @At("HEAD"))
    private void anextratouch$before(char c, int key, CallbackInfo ci) {
        anextratouch$text = Config.extraTyping ? anextratouch$text() : null;
        anextratouch$line = editLine;
    }

    @Inject(method = { "keyTyped", "func_73869_a" }, at = @At("RETURN"))
    private void anextratouch$after(char c, int key, CallbackInfo ci) {
        String before = anextratouch$text;
        anextratouch$text = null;
        String after = before == null ? null : anextratouch$text();
        if (after == null) return;
        TypingSounds.changed(
            before,
            after,
            anextratouch$line,
            editLine,
            anextratouch$line,
            editLine,
            c,
            key,
            GuiScreen.isCtrlKeyDown());
    }

    @Unique
    private String anextratouch$text() {
        if (anextratouch$broken) return null;
        try {
            if (anextratouch$tombstone == null) {
                anextratouch$tombstone = getClass().getDeclaredField("entitySign");
                anextratouch$tombstone.setAccessible(true);
                anextratouch$lines = anextratouch$tombstone.getType()
                    .getField("signText");
            }
            Object tombstone = anextratouch$tombstone.get(this);
            String[] lines = tombstone == null ? null : (String[]) anextratouch$lines.get(tombstone);
            return lines == null ? null : String.join("\n", lines);
        } catch (ReflectiveOperationException | ClassCastException e) {
            AnExtraTouch.LOG.warn("Project Zulu tombstone typing sounds are off: {}", e.toString());
            anextratouch$broken = true;
            return null;
        }
    }
}
