package top.dannystone.message.service;

import com.google.common.collect.Lists;
import top.dannystone.message.Message;
import top.dannystone.message.MessageCenter;
import top.dannystone.message.Subscriber;
import top.dannystone.message.Topic;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午11:15
 */
public class MessageService {
    List<Message> getLeftMessagesWithOffset(Subscriber subscriber, long offSet){
        Topic topic = subscriber.getTopic();
        List<Subscriber> subscribers = MessageCenter.topicSubscriberMap.get(topic);
        if(subscribers.contains(subscriber)){
            List<Message> messages = MessageCenter.topicMessageMap.get(topic);
            return messages.subList((int)offSet, messages.size());
        }
        return Lists.newArrayList();
    }

    List<Message> getAllMessages(Subscriber subscriber){
        return getLeftMessagesWithOffset(subscriber,0 );
    }
}
