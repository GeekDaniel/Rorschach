package top.dannystone.network.bizSocket;

import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import okio.BufferedSource;
import okio.ByteString;
import top.dannystone.network.bizSocket.bizsocketenum.PacketType;

import java.io.IOException;

public class MessagePacketFactory extends PacketFactory {
    @Override
    public bizsocket.tcp.Packet getRequestPacket(bizsocket.tcp.Packet reusable, Request request) {
        return new Packet(request.command(),request.body());
    }

    @Override
    public bizsocket.tcp.Packet getHeartBeatPacket(bizsocket.tcp.Packet reusable) {
        return new Packet(PacketType.HEARTBEAT.getCode(), ByteString.encodeUtf8("{}"));
    }

    @Override
    public bizsocket.tcp.Packet getRemotePacket(bizsocket.tcp.Packet reusable, BufferedSource source) throws IOException {
        if(source.exhausted()){
            return Packet.EMPTY;
        }
        return Packet.build(source);
    }
}
