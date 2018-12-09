package top.dannystone.message.service;

import top.dannystone.message.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午9:36
 */
public class MessageDispacher {
    public void dispatch(MessageChannel messageChannel){
        Topic topic = messageChannel.getTopic();
        Message message = messageChannel.getMessage();
        List<Subscriber> subscribers = MessageCenter.topicSubscriberMap.get(topic);
        if(subscribers!=null&&subscribers.size()>0){
           subscribers.stream().forEach((e)->{

           });
        }
    }
}
