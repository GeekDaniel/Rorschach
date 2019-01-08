package top.dannystone.message;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import top.dannystone.common.JedisClient.JedisListValueMap;
import top.dannystone.common.JedisClient.JedisSet;
import top.dannystone.common.JedisClient.JedisSetValueMap;
import top.dannystone.common.suger.FirstMeet;
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
    public static final Set<Topic> topics = new JedisSet();
    public static final Map<Topic, Set<Consumer>> topicConsumerMap = new JedisSetValueMap();
    public static final Map<Topic, List<Message>> topicMessageMap = new JedisListValueMap();
    public static final Map<Consumer, Integer> consumerOffsetMap = new ConcurrentHashMap<Consumer, Integer>();

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
            //同一个topic下面每一个consumer必须携带一个全局唯一标识符
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

    //此处的偏移量是指个数而非索引下标。偏移量=索引下标+1
    public List<Message> doConsume(Topic topic, Consumer consumer,Integer clientOffSet, int pollCount) {
        if(!checkConsume(topic, consumer)||!checkSubscribed(topic,consumer )){
           return easyError();
        }

        int serverOffSet = consumerOffsetMap.get(consumer)==null?0: consumerOffsetMap.get(consumer);
        List<Message> messages = topicMessageMap.get(topic);
        //如果clientOffSet为null，说明客户端未申明偏移量，按服务器偏移量来
        Optional<Integer> offSetOptional=FirstMeet
                .first(clientOffSet)
                .then(serverOffSet)
                .meet(e->e!=null&&e<messages.size()).get();
        if(!offSetOptional.isPresent()){
            return easyError();
        }
        Integer offset = offSetOptional.get();
        int fromIndex=offset;
        //越界检查
        int toIndex = (fromIndex + pollCount)>(messages.size())?(messages.size()):(offset + pollCount);
        //消费后调整offset
        consumerOffsetMap.put(consumer, toIndex);
        return messages.subList(fromIndex, toIndex);

    }

    public List<Message> easyError(){
        //目前校验不合法为返回空，后续改为报错
        return Lists.newArrayList();
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
        if (StringUtils.isBlank(topic.getName()) || consumer.getId() == 0) {
            return false;
        }
        return topics.contains(topic);
    }

    private boolean checkSubscribed(Topic topic, Consumer consumer) {
        Set<Consumer> consumers = topicConsumerMap.get(topic);
        return consumers.contains(consumer);
    }

}
