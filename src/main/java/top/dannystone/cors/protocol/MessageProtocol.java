package top.dannystone.cors.protocol;

import bizsocket.tcp.Packet;
import top.dannystone.message.Message;
import top.dannystone.message.Operation;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-20
 * Time: 上午12:40
 */
public class MessageProtocol {
    private Operation operation;
    private String content;

    public static boolean validate(Packet packet) {
        String content = packet.getDescription();
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(content);
        Integer code = jsonObject.getInteger("code");
        return Arrays.stream(Operation.values()).anyMatch(e->e.getCode()==code);
    }

    public static Message parseMessage(Packet packet) {
        String content = packet.getDescription();
        Message message = com.alibaba.fastjson.JSONObject.parseObject(content,Message.class);
        return message;
    }
}
