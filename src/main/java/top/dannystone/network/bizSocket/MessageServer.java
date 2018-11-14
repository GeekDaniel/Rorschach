package top.dannystone.network.bizSocket;

import com.google.common.collect.Lists;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import top.dannystone.cors.server.AbstractMessageServer;
import top.dannystone.message.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;


public class MessageServer extends AbstractMessageServer {
    private static final List<ConnectThread> connectThreads = new CopyOnWriteArrayList<ConnectThread>();

    private Queue<Message> messageBuffer = new ConcurrentLinkedQueue();

    class BizSocketMessageCloseableIterator extends MessageCloseableIterator {

        @Override
        public void close() throws IOException {

        }

        @Override
        public boolean hasNext() {
            return messageBuffer.peek() != null;
        }

        @Override
        public Message next() {
            return messageBuffer.poll();
        }
    }

    @Override
    public void doBoot(List<NodeConfig> nodeConfigs) {

        try {
            //todo 多节点处理
            ServerSocket serverSocket = new ServerSocket(nodeConfigs.get(0).getPort());
            boolean flag = true;
            while (flag) {
                Socket socket = serverSocket.accept();
                ConnectThread connectThread = new ConnectThread(socket);
                connectThread.start();
            }
        } catch (IOException e) {

        }

    }

    private static class ConnectThread extends Thread {
        Socket socket;
        boolean isRunning = true;
        BufferedSource reader;
        BufferedSink writer;

        public ConnectThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            connectThreads.add(this);
            try {
                System.out.println("accept: " + socket);
                reader = Okio.buffer(Okio.source(socket.getInputStream()));
                writer = Okio.buffer(Okio.sink(socket.getOutputStream()));
                while (isRunning) {
                    MessagePacket packet = MessagePacket.build(reader);
                    handleRequest(packet);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    socket = null;
                }

                connectThreads.remove(this);
            }
        }

        private void handleRequest(MessagePacket packet) throws IOException {
            System.out.println("handleRequest: " + packet);
            PacketType packetType = PacketType.fromValue(packet.cmd);
            switch (packetType) {
                case HEARTBEAT: {
                    //todo 后续根据策略再说
                }
                break;
                case BIZ_PACKACT: {
                    //mock biz message
                    Message message=new Message();
                    message.setMessage(String.valueOf(System.currentTimeMillis()));
                    message.setMessageType(MessageType.MESSAGE_DELIVER);
                    writePacket(new MessagePacket(PacketType.BIZ_PACKACT.getValue(),ByteString.encodeUtf8(com.alibaba.fastjson.JSONObject.toJSONString(message))));
                }
                break;
                default:
                    break;
                }

        }

        public void writePacket(MessagePacket packet) throws IOException {
            System.out.println("write packet: " + packet+", from thread:"+Thread.currentThread().getId());
            writer.write(packet.toBytes());
            writer.flush();
        }
    }

    public static String map2json(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"");
            sb.append(entry.getKey());
            sb.append("\"");
            sb.append(":");
            sb.append("\"");
            sb.append(entry.getValue());
            sb.append("\"");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

    public static Map<String, String> json2map(String json) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder(json);
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);

        String[] keyVale = sb.toString().split(",");
        //"${key}" : "${value}"
        for (String str : keyVale) {
            str = str.replaceAll("\"", "");
            map.put(str.split(":")[0], str.split(":")[1]);
        }
        return map;
    }

    public static void main(String[] args){
        MessageServer messageServer=new MessageServer();
        List<NodeConfig> nodeConfigs=Lists.newArrayList(new NodeConfig("127.0.0.1",56546));
        messageServer.doBoot(nodeConfigs);
    }
}
