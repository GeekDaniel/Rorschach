package top.dannystone.network;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tong on 16/9/30.
 */
public class MessageServer {
    private static final List<ConnectThread> connectThreads = new CopyOnWriteArrayList<ConnectThread>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9103);

        new QuoteThread().start();
        boolean flag = true;
        while (flag) {
            Socket socket = serverSocket.accept();
            ConnectThread connectThread = new ConnectThread(socket);
            connectThread.start();
        }
    }

    private static class QuoteThread extends Thread {
        @Override
        public void run() {
            List<ConnectThread> connections = connectThreads;

            boolean flag = true;
            while (flag) {
                DecimalFormat decimalFormat = new DecimalFormat("0.000");
                JSONObject params = new JSONObject();
                try {
                    params.put("code", "200");
                    params.put("result", decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                    params.put("lastPrice", decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (ConnectThread connectThread : connections) {
                    try {
                        connectThread.writePacket(new MessagePacket(MessageCmd.MESSAGE_TRANSFOR.getValue(), 0, params.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(new Random().nextInt(5000) + 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
            MessageCmd cmd = MessageCmd.fromValue(packet.cmd);
            switch (cmd) {
                case MESSAGE_TRANSFOR: {
                    DecimalFormat decimalFormat = new DecimalFormat("0.000");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("code", "200");
                    map.put("result", decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                    map.put("lastPrice", decimalFormat.format(((new Random().nextInt(500) + 4000) * 0.001)));
                    packet.setResponse(map);
                    writePacket(packet);
                }
                break;
                default:
                    break;
            }
        }

        public void writePacket(MessagePacket packet) throws IOException {
            System.out.println("write packet: " + packet);
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
}
