package top.dannystone.network;

import bizsocket.base.JSONRequestConverter;
import bizsocket.base.JSONResponseConverter;
import bizsocket.core.*;
import bizsocket.rx2.BizSocketRxSupport;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okio.ByteString;
import org.json.JSONException;
import org.json.JSONObject;

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
                .port(9103)
                .readTimeout(TimeUnit.SECONDS,30)
                .heartbeat(60)
                .build());

        //增加串行数据的处理(把两个命令返回的数据进行合并)
        client.addSerialSignal(new SerialSignal(MessageSerialContext.class, MessageCmd.MESSAGE_TRANSFOR.getValue(),
                new int[]{MessageCmd.MESSAGE_TRANSFOR.getValue(), MessageCmd.MESSAGE_TRANSFOR.getValue()}));

        client.getOne2ManyNotifyRouter().addStickyCmd(MessageCmd.MESSAGE_TRANSFOR.getValue(),new MessageBizPacketValidator());

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

        //创建rxjava请求环境(类似于retrofit)
        BizSocketRxSupport rxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(client)
                .build();
        MessageService service = rxSupport.create(MessageService.class);

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
        client.subscribe(client, MessageCmd.MESSAGE_TRANSFOR.getValue(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        String json = "{\"productId\" : \"1\",\"isJuan\" : \"0\",\"type\" : \"2\",\"sl\" : \"1\"}";
        client.request(new Request.Builder().command(MessageCmd.MESSAGE_TRANSFOR.getValue()).utf8body(json).build(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " attach: " + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });


        String params="HelloWorld";

        service.messageDelivery(params).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                System.out.println("rx response: " + jsonObject);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

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
