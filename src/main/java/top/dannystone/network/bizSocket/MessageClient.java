package top.dannystone.network.bizSocket;

import bizsocket.core.*;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import lombok.extern.slf4j.Slf4j;
import okio.ByteString;
import top.dannystone.message.*;
import top.dannystone.network.bizSocket.bizsocketenum.PacketType;

import java.util.concurrent.TimeUnit;

@Slf4j
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
                .readTimeout(TimeUnit.SECONDS, 30)
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

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " attach: " + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        };
        //注册
        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setOperation(Operation.REGISTER);
        Topic topic = new Topic("topic1");
        messageChannel.setTopic(topic);
        Consumer consumer = new Consumer(1);
        messageChannel.setConsumer(consumer);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageChannel)).build(), responseHandler);

        //生产
        MessageChannel messageChannel2 = new MessageChannel();
        messageChannel2.setOperation(Operation.PRODUCE);
        messageChannel2.setTopic(topic);
        Message message = new top.dannystone.message.Message(1, "hello world!");
        messageChannel2.setMessage(message);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageChannel2)).build(), responseHandler);

        //消费
        MessageChannel messageChannel3 = new MessageChannel();
        messageChannel3.setOperation(Operation.CONSUME);
        messageChannel3.setTopic(topic);
        messageChannel3.setConsumer(consumer);
        messageChannel3.setPollCount(100);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageChannel3)).build(), responseHandler);

    }
}
