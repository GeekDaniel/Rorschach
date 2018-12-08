package top.dannystone.network.bizSocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import top.dannystone.message.Message;
import top.dannystone.network.bizSocket.bizsocketenum.PacketType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class Packet extends bizsocket.tcp.Packet {

    static volatile int currentSeq = 0;
    public int length;//包大小
    public int cmd;//命令号
    public int seq;//包id  packetId

    public String content;

    public Packet() {
    }


    public Packet(int cmd, ByteString requestBody) {
        this.content = requestBody.utf8();
        this.cmd = cmd;
        this.seq = nextSeq();
    }

    private int nextSeq() {
        currentSeq++;
        if (currentSeq == Integer.MAX_VALUE) {
            currentSeq = 1;
        }
        return currentSeq;
    }

    public Packet(int cmd, int seq, String content) {
        this.cmd = cmd;
        this.seq = seq;
        this.content = content;
    }


    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(bos));

        try {
            //包长 = 内容长度 + 包头固定的12个字节
            ByteString byteString = ByteString.encodeUtf8(content);
            bufferedSink.writeInt(byteString.size() + 12);
            bufferedSink.writeInt(cmd);
            bufferedSink.writeInt(seq);
            bufferedSink.write(byteString);
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    public int getCommand() {
        return cmd;
    }

    @Override
    public void setCommand(int command) {
        this.cmd = command;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getPacketID() {
        return String.valueOf(seq);
    }

    @Override
    public void setPacketID(String packetID) {
        try {
            int id = Integer.valueOf(packetID);
            this.seq = id;
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "cmd=" + cmd +
                ", seq=" + seq +
                ", content='" + content + '\'' +
                '}';
    }

    public static Packet build(BufferedSource reader) throws IOException {
        Packet packet = new Packet();
        packet.length = reader.readInt();
        packet.cmd = reader.readInt();
        packet.seq = reader.readInt();
        //减去协议头的12个字节长度
        packet.content = reader.readString(packet.length - 12, Charset.forName("utf-8"));
        return packet;
    }

    public static Message toMessage(Packet packet) {
        if (packet == null) {
            return null;
        }

        if (packet.getCommand() == PacketType.BIZ_PACKACT.getCode()) {
            String content = packet.getContent();
            try {
                Message message = JSONObject.parseObject(content, Message.class);
                return message;
            }catch(Exception e){
                log.error("parse packet to message error :{}", content );
                return null;
            }
        }

        return null;

    }
}
