package top.dannystone.common.JedisClient;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2019-01-08
 * Time: 下午7:37
 */
public class JedisSetValueMap extends UnSupportMap {

    JedisClient jedisClient = new JedisClient();

    @Override
    public Object get(Object key) {
        Set<String> smembers = jedisClient.smembers(key.toString());
        return smembers.size()==0?null:smembers;
    }

    @Nullable
    @Override
    public Object put(Object key, Object value) {
        if (!(value instanceof Set)) {
            throw new RuntimeException("不是set");
        }
        Set<String> set= (Set<String>) value;
        List<String> stringList = set.stream().map(e -> JSONObject.toJSONString(e)).collect(Collectors.toList());
        String[] stringArray = (String[]) (stringList.toArray());
        return jedisClient.sadd(key.toString(), stringArray);
    }
}
