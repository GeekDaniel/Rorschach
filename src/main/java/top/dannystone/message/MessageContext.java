package top.dannystone.message;

import lombok.Data;

/**
 * 消息渠道定义了
 * 1.消息的类型
 * 2.消息的topic
 * 3.消息的内容
 * 一个注册消息 {"topic":"topic1","operation":"register"}
 * 一个发布消息{"topic":"topic1","messageId":"1","message":"message1","operation":"produce"}
 * 一个确认消息{"topic":"topic1","messageId":"1","operation":"ack"}
 * 消费消息 {"topic":"topic1","operation":"consume","pollCount":100}
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午8:40
 */
@Data
public class MessageContext {
    private Operation operation;
    private Topic topic;
    private Message message;
    private Consumer consumer;
    private Integer offSet;
    private int pollCount;
}
