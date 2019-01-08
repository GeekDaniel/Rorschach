package top.dannystone.common.JedisClient;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import top.dannystone.message.Consumer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-18
 * Time: 上午1:54
 */
public class Client {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379, 1000, 1000);

        Map<String, String> consumerOffsetMap = new ConcurrentHashMap<String, String>();
        consumerOffsetMap.put(JSONObject.toJSONString(new Consumer(1)), "1");
        consumerOffsetMap.put(JSONObject.toJSONString(new Consumer(2)), "2");
        String consumerOffsetMap1 = jedis.hmset("consumerOffsetMap", consumerOffsetMap);
        System.out.println(jedis.hget("consumerOffsetMap", JSONObject.toJSONString(new Consumer(2))));

    }

    public static Jedis jedis;

    public void init() {
        jedis = new Jedis("127.0.0.1", 6379, 1000, 1000);
    }

    public void check() {
        if (jedis == null) {
            throw new RuntimeException("jedis 未初始化化！");
        }
    }

    /**
     * set添加元素
     *
     * @param key
     * @param value
     */
    public void sadd(String key, String value) {
        check();
        jedis.sadd(key, value);
    }

    /**
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        check();
        Set<String> smembers = jedis.smembers(key);
        return smembers;
    }

    /**
     * append 向List添加一个
     *
     * @param key
     * @param value
     * @return
     */
    public Long rpush(String key, String value) {
        check();
        return jedis.rpush(key, value);
    }

    /**
     * 类似于sublist
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, int start, int end) {
        check();
        return jedis.lrange(key, start, end);
    }

//    public boolean hmset(String key,String value){
//        check();
//        String response = jedis.hmset("consumerOffsetMap",Maps.key, value);
//        return "OK".equals(response);
//    }



}
