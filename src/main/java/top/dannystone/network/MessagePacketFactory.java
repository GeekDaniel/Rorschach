package top.dannystone.network;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import okio.BufferedSource;
import okio.ByteString;

import java.io.IOException;

public class MessagePacketFactory extends PacketFactory {
    @Override
    public Packet getRequestPacket(Packet reusable,Request request) {
        return new MessagePacket(request.command(),request.body());
    }

    @Override
    public Packet getHeartBeatPacket(Packet reusable) {
        return new MessagePacket(MessageCmd.HEARTBEAT.getValue(), ByteString.encodeUtf8("{}"));
    }

    @Override
    public Packet getRemotePacket(Packet reusable,BufferedSource source) throws IOException {
        return MessagePacket.build(source);
    }
}
