package top.dannystone.network.bizSocket;

import bizsocket.core.AbstractSerialContext;
import bizsocket.core.RequestContext;
import bizsocket.core.RequestQueue;
import bizsocket.core.SerialSignal;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageSerialContext extends AbstractSerialContext {
    private static final String TAG = MessageSerialContext.class.getSimpleName();

    private MessagePacket messagePacket;
    private int[] messageIdArr;
    private int[] orderQuerySeqArr;

    private Map<Integer,Packet> orderTypeMap = new ConcurrentHashMap<>();

    public MessageSerialContext(SerialSignal serialSignal, RequestContext requestContext) {
        super(serialSignal, requestContext);
    }

    @Override
    public String getRequestPacketId() {
        String packetId = getRequestContext().getRequestPacket().getPacketID();
        return packetId;
    }

    @Override
    public boolean shouldProcess(RequestQueue requestQueue, Packet packet) {
        boolean result = super.shouldProcess(requestQueue,packet);
        if (!result) {
            return false;
        }

        MessagePacket responsePacket = (MessagePacket) packet;
        JSONObject obj = null;
        try {
            obj = new JSONObject(responsePacket.getContent());
        } catch (JSONException e) {
            return false;
        }
        if (packet.getCommand() == PacketType.BIZ_PACKACT.getValue()) {
            if (!MessageProtocolUtil.isSuccessResponsePacket(responsePacket)) {
                return false;
            }
            try {
                JSONArray resultArr = obj.optJSONArray("result");

                PacketFactory packetFactory = requestQueue.getBizSocket().getPacketFactory();
                if (resultArr == null || resultArr.length() == 0 || packetFactory == null) {
                    throw new Exception("");
                }

                messagePacket = (MessagePacket) packet;
                messageIdArr = new int[resultArr.length()];
                orderQuerySeqArr = new int[resultArr.length()];
                for (int i = 0;i < resultArr.length();i++) {
                    JSONObject order = resultArr.optJSONObject(i);
                    if (order == null) {
                        continue;
                    }

                    int orderId = order.optInt("orderId",-1);
                    if (orderId == -1) {
                        continue;
                    }

                    messageIdArr[i] = orderId;
                    //发起查询订单类型的查询
                    MessagePacket MessagePacket = buildQueryOrderTypePacket(requestQueue,orderId);

                    orderQuerySeqArr[i] = Integer.valueOf(MessagePacket.getPacketID());
                    if (MessagePacket != null) {
                        System.out.println("同步请求订单类型: " + MessagePacket.getContent());
                        requestQueue.getBizSocket().getSocketConnection().sendPacket(MessagePacket);
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private MessagePacket buildQueryOrderTypePacket(RequestQueue requestQueue, int orderId) {
        JSONObject params = new JSONObject();
        try {
            params.put("orderId",String.valueOf(orderId));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return (MessagePacket) requestQueue.getBizSocket().getPacketFactory().getRequestPacket(new Request.Builder().command(PacketType.BIZ_PACKACT.getValue()).utf8body(params.toString()).build());
        } catch (Throwable e) {

        }

        return null;
    }

    @Override
    public Packet processPacket(RequestQueue requestQueue, Packet packet) {
        if (messagePacket != null && messageIdArr != null && orderTypeMap.size() == messageIdArr.length) {
            try {
                JSONObject obj = new JSONObject(messagePacket.getContent());
                JSONArray resultArr = obj.optJSONArray("result");
                for (int i = 0;i < resultArr.length();i++) {
                    JSONObject order = resultArr.optJSONObject(i);
                    int orderId = order.optInt("orderId");

                    JSONObject orderType = new JSONObject(((MessagePacket)orderTypeMap.get(orderId)).getContent());
                    order.put("orderType",orderType.optInt("ordertype",0));
                    order.put("orderTypeRes",orderType);
                }

                MessagePacket MessagePacket = (MessagePacket) packet;
                MessagePacket.setCommand(messagePacket.getCommand());
                MessagePacket.setContent(obj.toString());

                System.out.println("合并订单列表和类型: " + MessagePacket.getContent());
                return MessagePacket;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
