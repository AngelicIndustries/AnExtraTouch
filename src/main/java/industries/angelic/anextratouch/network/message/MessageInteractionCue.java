package industries.angelic.anextratouch.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import industries.angelic.anextratouch.compat.InteractionCues;
import io.netty.buffer.ByteBuf;

public class MessageInteractionCue implements IMessage {

    public int cue, x, y, z;

    public MessageInteractionCue() {}

    public MessageInteractionCue(InteractionCues.Cue cue, int x, int y, int z) {
        this.cue = cue.ordinal();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        cue = buf.readUnsignedByte();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(cue);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
