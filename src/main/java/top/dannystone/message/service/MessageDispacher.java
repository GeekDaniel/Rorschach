package top.dannystone.message.service;

import lombok.extern.slf4j.Slf4j;
import top.dannystone.exception.ConsumerDuplicateException;
import top.dannystone.exception.InvalidRegistException;
import top.dannystone.message.*;
import top.dannystone.message.domain.DispatchResponse;

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

    public DispatchResponse dispatch(MessageContext messageContext) {
        Operation operation = messageContext.getOperation();
        Consumer consumer = messageContext.getConsumer();
        Message message = messageContext.getMessage();
        int pollCount = messageContext.getPollCount();
        Topic topic1 = messageContext.getTopic();
        switch (operation) {
            case REGISTER:
                doRegister(topic1, consumer);
                return new DispatchResponse(Operation.REGISTER,null);
            case ACK:
                doAck();
                return new DispatchResponse(Operation.ACK,null);
            case PRODUCE:
                doProduce(topic1, message);
                return new DispatchResponse(Operation.PRODUCE,null);
            case CONSUME:
                List<Message> messages = doConsume(topic1, consumer, pollCount == 0 ? default_pollCount : pollCount);
                return new DispatchResponse(Operation.CONSUME,messages);
            default:
                return new DispatchResponse(null,null);
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
