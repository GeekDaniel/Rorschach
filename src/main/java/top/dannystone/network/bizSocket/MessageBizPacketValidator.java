package top.dannystone.network.bizSocket;

import bizsocket.core.PacketValidator;

public class MessageBizPacketValidator implements PacketValidator {

    @Override
    public boolean verify(bizsocket.tcp.Packet packet) {
        return MessageProtocolUtil.isSuccessResponsePacket((Packet) packet);
    }
}
