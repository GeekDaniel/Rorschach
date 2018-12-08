package top.dannystone.network.bizSocket;
import top.dannystone.message.MessageType;

import bizsocket.base.JSONRequestConverter;
import bizsocket.base.JSONResponseConverter;
import bizsocket.core.*;
import bizsocket.rx2.BizSocketRxSupport;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import okio.ByteString;
import top.dannystone.message.Message;
import top.dannystone.network.bizSocket.bizsocketenum.PacketType;

import java.util.concurrent.TimeUnit;

public class MessageClient extends AbstractBizSocket {
    public MessageClient(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return new MessagePacketFactory();
    }

    public static void main(String[] args) {
        MessageClient client = new MessageClient(new Configuration.Builder()
                .host("127.0.0.1")
                .port(56546)
                .readTimeout(TimeUnit.SECONDS,30)
                .heartbeat(60)
                .build());

        client.getInterceptorChain().addInterceptor(new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                System.out.println("发现一个请求postRequestHandle: " + context);
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                System.out.println("收到一个包postResponseHandle: " + responsePacket);
                return false;
            }
        });

        try {
            //连接
            client.connect();
            //启动断线重连
            client.getSocketConnection().bindReconnectionManager();
            //开启心跳
            client.getSocketConnection().startHeartBeat();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //注册通知
        client.subscribe(client, PacketType.BIZ_PACKACT.getCode(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        //init a message
        Message message=new Message();
        message.setMessage("hello world!");
        message.setMessageType(MessageType.REGISTER);
        message.setMessageId(1);


        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(message)).build(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " attach: " + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });



        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
