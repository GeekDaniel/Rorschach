package top.dannystone.message.service;

import com.google.common.collect.Lists;
import top.dannystone.message.Message;
import top.dannystone.message.Consumer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午11:15
 */
public class MessageService {
    List<Message> getLeftMessagesWithOffset(Consumer consumer, long offSet){
//        Topic topic = consumer.getTopic();
//        List<Consumer> consumers = JavaMessageCenter.topicConsumerMap.get(topic);
//        if(consumers.contains(consumer)){
//            List<Message> messages = JavaMessageCenter.topicMessageMap.get(topic);
//            return messages.subList((int)offSet, messages.size());
//        }
        return Lists.newArrayList();
    }

    List<Message> getAllMessages(Consumer consumer){
        return getLeftMessagesWithOffset(consumer,0 );
    }
}
