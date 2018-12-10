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
    public static final Map<Topic, List<Consumer>> topicSubscriberMap = new ConcurrentHashMap<Topic, List<Consumer>>();
    public static final Map<Topic, List<Message>> topicMessageMap = new ConcurrentHashMap<Topic, List<Message>>();
    public static final Map<Consumer, Long> topicOffsiteMap = new ConcurrentHashMap<Consumer, Long>();


    /**
     * 订阅要做的几件事
     * 1.添加一个订阅者
     * @param topic
     * @param consumer
     * @throws InvalidRegistException
     */
    public void subscirbe(Topic topic, Consumer consumer) throws InvalidRegistException {
        if (!checkRegist(topic, consumer)) {
            throw new InvalidRegistException("缺少合法的Topic或订阅者！");
        }

        //添加一个订阅者
        List<Consumer> consumers = topicSubscriberMap.get(topic);
        consumers.add(consumer);
    }

    /**
     * 一个合法的订阅需要校验
     * 1.topic已存在
     * 2.订阅者合法
     */
    private boolean checkRegist(Topic topic, Consumer consumer) {
        if (StringUtils.isBlank(topic.getName()) || consumer.getIdentifier() == null) {
            return false;
        }
        return topics.contains(topic);
    }


}
