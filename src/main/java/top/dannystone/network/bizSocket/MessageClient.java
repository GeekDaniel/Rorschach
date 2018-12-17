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
        MessageContext messageContext = new MessageContext();
        messageContext.setOperation(Operation.REGISTER);
        Topic topic = new Topic("topic1");
        messageContext.setTopic(topic);
        Consumer consumer = new Consumer(1);
        messageContext.setConsumer(consumer);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageContext)).build(), responseHandler);

        //生产
        MessageContext messageContext2 = new MessageContext();
        messageContext2.setOperation(Operation.PRODUCE);
        messageContext2.setTopic(topic);
        Message message = new top.dannystone.message.Message(1, "hello world!");
        messageContext2.setMessage(message);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageContext2)).build(), responseHandler);

        //消费
        MessageContext messageContext3 = new MessageContext();
        messageContext3.setOperation(Operation.CONSUME);
        messageContext3.setTopic(topic);
        messageContext3.setConsumer(consumer);
        messageContext3.setPollCount(100);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageContext3)).build(), responseHandler);

        //重复消费
        MessageContext messageContext4 = new MessageContext();
        messageContext4.setOperation(Operation.CONSUME);
        messageContext4.setTopic(topic);
        messageContext4.setConsumer(consumer);
        messageContext4.setPollCount(100);
        messageContext4.setOffSet(0);
        client.request(new Request.Builder().command(PacketType.BIZ_PACKACT.getCode()).utf8body(com.alibaba.fastjson.JSONObject.toJSONString(messageContext4)).build(), responseHandler);
    }
}
