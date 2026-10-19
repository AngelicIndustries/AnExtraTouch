package industries.angelic.anextratouch.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import industries.angelic.anextratouch.network.message.MessageArmorStep;

public class HandlerArmorStep implements IMessageHandler<MessageArmorStep, IMessage> {

    @Override
    public IMessage onMessage(MessageArmorStep message, MessageContext ctx) {
        // Decode-only compatibility with older servers. Past Footsteps owns movement accents.
        return null;
    }
}
