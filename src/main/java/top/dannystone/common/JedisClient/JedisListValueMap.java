package top.dannystone.common.JedisClient;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2019-01-08
 * Time: 下午1:27
 */
public class JedisListValueMap extends UnSupportMap {


    JedisClient jedisClient = new JedisClient();


    @Override
    public Object get(Object key) {
        //todo ? int long
        return jedisClient.lrange(key.toString(), 0, (int) (jedisClient.llen(key.toString()).longValue()));
    }

    @Nullable
    @Override
    public Object put(Object key, Object value) {
        if (!(value instanceof List)) {
            throw new RuntimeException("不是list");
        }
        ArrayList<String> list = (ArrayList<String>) value;
        List<String> stringList = list.stream().map(e -> JSONObject.toJSONString(e)).collect(Collectors.toList());
        String[] stringArray = (String[]) (stringList.toArray());
        return jedisClient.lPush(key.toString(), stringArray);
    }


}
