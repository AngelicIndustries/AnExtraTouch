package industries.angelic.anextratouch.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundEventAccessorComposite;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.compat.InteractionCues;
import industries.angelic.anextratouch.handlers.client.extrasounds.ExtraSounds;
import industries.angelic.anextratouch.network.message.MessageInteractionCue;

public class HandlerInteractionCue implements IMessageHandler<MessageInteractionCue, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageInteractionCue message, MessageContext ctx) {
        InteractionCues.Cue cue = InteractionCues.Cue.byId(message.cue);
        if (cue == null) return null;
        // Packets arrive on the network thread; the sound system belongs to the client thread.
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                if (AnExtraTouch.isDebug()) AnExtraTouch.debug(
                    "Interaction cue " + cue
                        + " received at "
                        + message.x
                        + ", "
                        + message.y
                        + ", "
                        + message.z
                        + "; extraModInteractions="
                        + Config.extraModInteractions
                        + ", "
                        + cue.event
                        + (Minecraft.getMinecraft()
                            .getSoundHandler()
                            .getSound(ExtraSounds.id(cue.event)) != null ? " is defined" : " is MISSING"));
                if (!Config.extraModInteractions) return;
                float pitch = 1 + ((float) Math.random() - 0.5F) * cue.spread;
                AnExtraTouch.debug("Playing " + cue.event + " at pitch " + pitch);
                ExtraSounds.at(cue.event, message.x + 0.5, message.y + 0.5, message.z + 0.5, 1, pitch);
                // A companion event plays alongside, as the inventory accents do.
                String accent = cue.event + ".accent";
                SoundEventAccessorComposite companion = Minecraft.getMinecraft()
                    .getSoundHandler()
                    .getSound(ExtraSounds.id(accent));
                if (companion != null && companion.func_148721_a() > 0)
                    ExtraSounds.at(accent, message.x + 0.5, message.y + 0.5, message.z + 0.5, 1, pitch);
            });
        return null;
    }
}
