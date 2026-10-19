package industries.angelic.anextratouch.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.anextratouch.Config;
import industries.angelic.anextratouch.handlers.client.camera.ScreenShakeManager;
import industries.angelic.anextratouch.network.message.MessageExplosionShake;

public class HandlerExplosionShake implements IMessageHandler<MessageExplosionShake, IMessage> {

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final MessageExplosionShake message, MessageContext ctx) {

        if (Config.cameraOverhaulEnabled) {
            ScreenShakeManager.Slot shake = ScreenShakeManager.createDirect();
            shake.position.set(message.posX, message.posY, message.posZ);
            shake.radius = message.size * 10f;
            shake.trauma = Config.cameraExplosionTrauma;
            shake.lengthInSeconds = Config.cameraExplosionLength;
        }

        return null;
    }

}
