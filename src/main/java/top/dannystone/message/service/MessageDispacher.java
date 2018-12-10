package top.dannystone.message.service;

import lombok.extern.slf4j.Slf4j;
import top.dannystone.exception.ConsumerDuplicateException;
import top.dannystone.exception.InvalidRegistException;
import top.dannystone.message.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午9:36
 */
@Slf4j
public class MessageDispacher {
    private static int default_pollCount=10;
    MessageCenter messageCenter = new MessageCenter();

    public void dispatch(MessageChannel messageChannel) {
        Operation operation = messageChannel.getOperation();
        Consumer consumer = messageChannel.getConsumer();
        Message message = messageChannel.getMessage();
        int pollCount = messageChannel.getPollCount();
        Topic topic1 = messageChannel.getTopic();
        switch (operation) {
            case REGISTER:
                doRegister(topic1, consumer);
                break;
            case ACK:
                doAck();
                break;
            case PRODUCE:
                doProduce(topic1, message);
                break;
            case CONSUME:
                doConsume(topic1, consumer,pollCount==0?default_pollCount:pollCount );
                break;
            default:
                break;
        }

    }

    private void doRegister(Topic topic, Consumer consumer) {
        try {
            messageCenter.subscirbe(topic, consumer);
        } catch (InvalidRegistException e) {
            //todo return a error to client
            e.printStackTrace();
        } catch (ConsumerDuplicateException e) {
            //todo return a error to client
            e.printStackTrace();
        }
    }

    private void doAck() {
        //目前默认是客户端自动ack
    }

    private void doProduce(Topic topic, Message message) {
        messageCenter.doProduce(topic, message);
    }

    private List<Message> doConsume(Topic topic, Consumer consumer, int pollCount) {
        return messageCenter.doConsume(topic, consumer, pollCount);
    }
}
