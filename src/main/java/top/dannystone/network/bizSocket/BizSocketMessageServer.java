package top.dannystone.network.bizSocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import top.dannystone.cors.server.AbstractMessageServer;
import top.dannystone.message.MessageChannel;
import top.dannystone.message.NodeConfig;
import top.dannystone.message.Operation;
import top.dannystone.message.domain.DispatchResponse;
import top.dannystone.message.service.MessageDispacher;
import top.dannystone.network.bizSocket.bizsocketenum.PacketType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class BizSocketMessageServer extends AbstractMessageServer {
    private static final List<ConnectThread> connectThreads = new CopyOnWriteArrayList<ConnectThread>();

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
        MessageDispacher messageDispacher=new MessageDispacher();

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
                    System.out.println("packet:" + packet);
                    handlePacket(packet);
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
                MessageChannel messageChannel = Packet.toMessageChannel(packet);
                DispatchResponse dispatchResponse = messageDispacher.dispatch(messageChannel);
                if(dispatchResponse.getOperation()==Operation.CONSUME){
                    Packet packet1=new Packet(PacketType.BIZ_PACKACT.getCode(),ByteString.encodeUtf8(JSONObject.toJSONString(dispatchResponse.getResponse())));
                    try {
                        writePacket(packet1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
