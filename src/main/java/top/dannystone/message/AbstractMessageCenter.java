package top.dannystone.message;

import com.google.common.collect.Lists;
import top.dannystone.common.JedisClient.JedisListValueMap;
import top.dannystone.common.JedisClient.JedisSet;
import top.dannystone.common.JedisClient.JedisSetValueMap;
import top.dannystone.exception.ConsumerDuplicateException;
import top.dannystone.exception.InvalidRegistException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2019-01-20
 * Time: 下午12:47
 */
public abstract class AbstractMessageCenter {

    public void subscirbe(Topic topic, Consumer consumer) throws InvalidRegistException, ConsumerDuplicateException{
        throw new UnsupportedOperationException();
    }

    public void doProduce(Topic topic, Message message){
        throw new UnsupportedOperationException();
    }

    //此处的偏移量是指个数而非索引下标。偏移量=索引下标+1
    public List<Message> doConsume(Topic topic, Consumer consumer, Integer clientOffSet, int pollCount){
        throw new UnsupportedOperationException();
    }

    public List<Message> easyError() {
        System.out.println("error xxxxxxxxx");
        //目前校验不合法为返回空，后续改为报错
        return Lists.newArrayList();
    }
}
