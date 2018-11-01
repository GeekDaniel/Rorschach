package top.dannystone.network;

import bizsocket.core.PacketValidator;
import bizsocket.tcp.Packet;

public class MessageBizPacketValidator implements PacketValidator {

    @Override
    public boolean verify(Packet packet) {
        return MessageProtocolUtil.isSuccessResponsePacket((MessagePacket) packet);
    }
}
