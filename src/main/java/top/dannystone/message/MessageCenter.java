package top.dannystone.message;

import org.apache.commons.lang3.StringUtils;
import top.dannystone.exception.InvalidRegistException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-01
 * Time: 下午11:14
 */
public class MessageCenter {
    public static final Set<Topic> topics = Collections.synchronizedSet(new HashSet<Topic>());
    public static final Map<Topic, List<Subscriber>> topicSubscriberMap = new ConcurrentHashMap<Topic, List<Subscriber>>();
    public static final Map<Topic, List<Message>> topicMessageMap = new ConcurrentHashMap<Topic, List<Message>>();
    public static final Map<Subscriber, Long> topicOffsiteMap = new ConcurrentHashMap<Subscriber, Long>();


    public void subscirbe(Topic topic, Subscriber subscriber) throws InvalidRegistException {
        if (!checkRegist(topic, subscriber)) {
            throw new InvalidRegistException("缺少合法的Topic或订阅者！");
        }
        List<Subscriber> subscribers = topicSubscriberMap.get(topic);
        subscribers.add(subscriber);
    }

    /**
     * 一个合法的订阅需要校验
     * 1.topic已存在
     * 2.订阅者合法
     */
    private boolean checkRegist(Topic topic, Subscriber subscriber) {
        if (StringUtils.isBlank(topic.getName()) || subscriber.getIdentifier() == null) {
            return false;
        }
        return topics.contains(topic);
    }


}
