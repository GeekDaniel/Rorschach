package top.dannystone.message;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import top.dannystone.exception.ConsumerDuplicateException;
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
    public static final Map<Topic, Set<Consumer>> topicConsumerMap = new ConcurrentHashMap<Topic, Set<Consumer>>();
    public static final Map<Topic, List<Message>> topicMessageMap = new ConcurrentHashMap<Topic, List<Message>>();
    public static final Map<Consumer, Integer> topicOffsiteMap = new ConcurrentHashMap<Consumer, Integer>();


    /**
     * 订阅要做的几件事
     * 1.添加一个订阅者
     *
     * @param topic
     * @param consumer
     * @throws InvalidRegistException
     */
    public void subscirbe(Topic topic, Consumer consumer) throws InvalidRegistException, ConsumerDuplicateException {
        if (!checkSubscribe(topic, consumer)) {
            throw new InvalidRegistException("缺少合法的Topic或订阅者！");
        }

        Set<Consumer> consumers;
        synchronized (MessageCenter.class) {
            consumers = topicConsumerMap.get(topic);
            if (consumers == null) {
                topicConsumerMap.put(topic, Sets.newConcurrentHashSet());
                consumers = topicConsumerMap.get(topic);
            }
        }
        if (consumers.contains(consumer)) {
            throw new ConsumerDuplicateException("订阅者必须唯一！");
        }
        consumers.add(consumer);
    }

    public void doProduce(Topic topic, Message message) {
        List<Message> messageList;
        synchronized (MessageCenter.class) {
            messageList = topicMessageMap.get(topic);
            if (messageList == null) {
                //性能
                topicMessageMap.put(topic, Lists.newCopyOnWriteArrayList());
                messageList = topicMessageMap.get(topic);
            }
        }
        messageList.add(message);
    }

    public List<Message> doConsume(Topic topic, Consumer consumer, int pollCount) {
        checkConsume(topic, consumer);
        Integer offSet = topicOffsiteMap.get(topic);
        List<Message> messages = topicMessageMap.get(topic);
        int toIndex = (offSet + pollCount)>(messages.size()-1)?(messages.size()-1):(offSet + pollCount);
        return messages.subList(offSet, toIndex);

    }

    private boolean checkConsume(Topic topic, Consumer consumer) {
        if (!checkSubscribe(topic, consumer)) {
            return false;
        }
        return topicConsumerMap.get(topic) != null && topicConsumerMap.get(topic).contains(consumer);
    }

    /**
     * 一个合法的订阅需要校验
     * 1.topic已存在
     * 2.订阅者合法
     */
    private boolean checkSubscribe(Topic topic, Consumer consumer) {
        if (StringUtils.isBlank(topic.getName()) || consumer.getId() != 0) {
            return false;
        }
        return topics.contains(topic);
    }


}
