package top.dannystone.network.bizSocket;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import top.dannystone.cors.server.AbstractMessageServer;
import top.dannystone.message.Message;
import top.dannystone.message.MessageIterator;
import top.dannystone.message.NodeConfig;
import top.dannystone.network.bizSocket.bizsocketenum.PacketType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class MessageServer extends AbstractMessageServer {
    private static final List<ConnectThread> connectThreads = new CopyOnWriteArrayList<ConnectThread>();


    static class BizSocketMessageIterator extends MessageIterator {

        Queue<Message> messageBuffer = new ConcurrentLinkedQueue<>();

        @Override
        public boolean hasNext() {
            return messageBuffer.peek() != null;
        }

        @Override
        public Message next() {
            //一次性消费
            return messageBuffer.poll();
        }

        public void add(Message message) {
            messageBuffer.add(message);
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

        BizSocketMessageIterator messageIterator = new BizSocketMessageIterator();

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
                    Packet packet = Packet.build(reader);
                    handlePacket(packet);
                    System.out.println("packet:" + packet);
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

        //                    writePacket(new Packet(PacketType.BIZ_PACKACT.getValue(),ByteString.encodeUtf8(com.alibaba.fastjson.JSONObject.toJSONString(message))));

        public void writePacket(Packet packet) throws IOException {
            System.out.println("write packet: " + packet + ", from thread:" + Thread.currentThread().getId());
            writer.write(packet.toBytes());
            writer.flush();
        }

        //将Packet 封装成message并添加到迭代器中
        private void handlePacket(Packet packet) {
            if (packet == null) {
                return;
            }
            if (packet.getCommand() == PacketType.BIZ_PACKACT.getCode()) {
                Message message = Packet.toMessage(packet);
                messageIterator.add(message);
                log.info("message receiced: ", message);
            }
        }
    }

    public static void main(String[] args) {
        MessageServer messageServer = new MessageServer();
        List<NodeConfig> nodeConfigs = Lists.newArrayList(new NodeConfig("127.0.0.1", 56546));
        messageServer.doBoot(nodeConfigs);
    }
}
