package top.dannystone.message.service;

import lombok.extern.slf4j.Slf4j;
import top.dannystone.message.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午9:36
 */
@Slf4j
public class MessageDispacher {
    public void dispatch(MessageChannel messageChannel) {
        Operation operation = messageChannel.getOperation();
        Topic topic1 = messageChannel.getTopic();
        switch (operation) {
            case REGISTER:
                doRegister(topic1, Sub);
                break;
            case ACK:
                doAck();
                break;
            case MESSAGE_DELIVER:
                doMessageDeliver();
                break;
            default:
                break;
        }

    }

    private void doRegister(Topic topic, Consumer consumer) {

    }

    private void doAck(Topic topic, Consumer consumer) {

    }

    private void doMessageDeliver(Topic topic, Consumer consumer) {

    }
}
