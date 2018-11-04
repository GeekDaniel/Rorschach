package top.dannystone.message;

import lombok.Data;
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
    private static final Set<Topic> topics=Collections.synchronizedSet(new HashSet<Topic>());
    private static final Map<Topic,List<Message>> messageMap=new ConcurrentHashMap<Topic, List<Message>>();
    public void regist(Topic topic, Subscriber subscriber)throws InvalidRegistException{
        if(checkRegist(topic,subscriber)){
            topics.add(topic);
        }
        throw new InvalidRegistException("缺少合法的Topic或订阅者！");
    }

    private boolean checkRegist(Topic topic,Subscriber subscriber){
        if(StringUtils.isBlank(topic.getName())||subscriber.getIdentifier()==null){
            return false;
        }
        return true;
    }


}
