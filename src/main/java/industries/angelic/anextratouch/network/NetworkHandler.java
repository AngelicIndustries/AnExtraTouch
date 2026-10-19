package industries.angelic.anextratouch.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import industries.angelic.anextratouch.AnExtraTouch;
import industries.angelic.anextratouch.network.handler.HandlerArmorStep;
import industries.angelic.anextratouch.network.handler.HandlerExplosionShake;
import industries.angelic.anextratouch.network.handler.HandlerHello;
import industries.angelic.anextratouch.network.message.MessageArmorStep;
import industries.angelic.anextratouch.network.message.MessageExplosionShake;
import industries.angelic.anextratouch.network.message.MessageHello;

public class NetworkHandler {

    private static final int LEGACY_HELLO = 0;
    private static final int LEGACY_ARMOR_STEP = 1;
    private static final int EXPLOSION_SHAKE = 2;
    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(AnExtraTouch.MODID);

    public static void init() {
        // IDs 0 and 1 remain decode-only so clients can safely ignore packets from older AET servers.
        channel.registerMessage(HandlerHello.class, MessageHello.class, LEGACY_HELLO, Side.CLIENT);
        channel.registerMessage(HandlerArmorStep.class, MessageArmorStep.class, LEGACY_ARMOR_STEP, Side.CLIENT);
        channel.registerMessage(HandlerExplosionShake.class, MessageExplosionShake.class, EXPLOSION_SHAKE, Side.CLIENT);
    }
}
