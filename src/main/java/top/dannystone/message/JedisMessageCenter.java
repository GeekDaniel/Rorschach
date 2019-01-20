package top.dannystone.message;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import top.dannystone.common.JedisClient.JedisClient;
import top.dannystone.common.suger.FirstMeet;
import top.dannystone.exception.ConsumerDuplicateException;
import top.dannystone.exception.InvalidRegistException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2019-01-20
 * Time: 下午12:48
 */

public class JedisMessageCenter extends AbstractMessageCenter {
    private static final String CONSUMER_PREFIX = "CONSUMER_";
    private static final String MESSAGE_PREFIX = "MESSAGE_";
    private static final String OFFSET = "OFFSET";

    @Override
    public void subscirbe(Topic topic, Consumer consumer) throws InvalidRegistException, ConsumerDuplicateException {
        if (!checkSubscribe(topic, consumer)) {
            throw new InvalidRegistException("缺少合法的Topic或订阅者！");
        }

        Set<String> consumers = JedisClient.smembers(topic.getName());
        if (consumers.contains(consumer)) {
            //同一个topic下面每一个consumer必须携带一个全局唯一标识符
            throw new ConsumerDuplicateException("订阅者必须唯一！");
        }
        JedisClient.sadd(genKeyWithPrefix(topic, CONSUMER_PREFIX), String.valueOf(consumer.getId()));
    }

    @Override
    public void doProduce(Topic topic, Message message) {
        JedisClient.lPush(genKeyWithPrefix(topic, MESSAGE_PREFIX), JSONObject.toJSONString(message));
    }

    @NotNull
    private String genKeyWithPrefix(Topic topic, String messagePrefix) {
        return messagePrefix + topic.getName();
    }

    @Override
    public List<Message> doConsume(Topic topic, Consumer consumer, Integer clientOffSet, int pollCount) {

        String topicConsumer = topic.getName() + "_" + consumer.getId();
        if (!checkSubscribed(topic, consumer)) {
            return easyError();
        }

        int serverOffSet = JedisClient.hmget(OFFSET, topicConsumer);
        String messageKey = genKeyWithPrefix(topic, MESSAGE_PREFIX);
        //如果clientOffSet为null，说明客户端未申明偏移量，按服务器偏移量来
        Optional<Integer> offSetOptional = FirstMeet
                .first(clientOffSet)
                .then(serverOffSet)
                .meet(e -> e != null ).get();
        Integer offset =  offSetOptional.isPresent()?offSetOptional.get():0;
        int fromIndex = offset;
        //越界检查
        int toIndex = (fromIndex + pollCount);
        List<Message> messages = JedisClient.lrange(messageKey, fromIndex, toIndex).stream().map(e -> JSONObject.parseObject(e, Message.class)).collect(Collectors.toList());
        if (!offSetOptional.isPresent()) {
            return easyError();
        }
        //计算真实偏移量
        toIndex = fromIndex+messages.size();
        //消费后调整offset
        JedisClient.hmset(OFFSET,topicConsumer ,String.valueOf(toIndex) );
        return messages;
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
        return true;
    }

    private boolean checkSubscribed(Topic topic, Consumer consumer) {
        return JedisClient.smembers(topic.getName()).contains(consumer.getId());
    }


}
