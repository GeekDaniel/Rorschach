package top.dannystone.common.JedisClient;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
public class JedisClient {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379, 1000, 1000);

        Map<String, String> consumerOffsetMap = new ConcurrentHashMap<String, String>();
        consumerOffsetMap.put(JSONObject.toJSONString(new Consumer(1)), "1");
        consumerOffsetMap.put(JSONObject.toJSONString(new Consumer(2)), "2");
        String consumerOffsetMap1 = jedis.hmset("consumerOffsetMap", consumerOffsetMap);
        System.out.println(jedis.hget("consumerOffsetMap", JSONObject.toJSONString(new Consumer(2))));

    }

    public static Jedis jedis = new Jedis("127.0.0.1", 6379, 1000, 1000);

    private static void check() {
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
    public static boolean sadd(String key, String value) {
        check();
        return jedis.sadd(key, value) == 1;
    }

    /**
     * @param key
     * @param values
     * @return
     */
    public static boolean sadd(String key, String[] values) {
        check();
        return jedis.sadd(key, values) == 1;
    }

    /**
     * @param key
     * @return
     */
    public static Set<String> smembers(String key) {
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
    public static Long rpush(String key, String value) {
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
    public static List<String> lrange(String key, int start, int end) {
        check();
        return jedis.lrange(key, start, end);
    }

//    public boolean hmset(String key,String value){
//        check();
//        String response = jedis.hmset("consumerOffsetMap",Maps.key, value);
//        return "OK".equals(response);
//    }

    /**
     * @param key
     * @return
     */
    public static String type(String key) {
        check();
        return jedis.type(key);
    }

    public static Long lPush(String key, String[] values) {
        check();
        return jedis.lpush(key, values);
    }

    public static Long lPush(String key, String value) {
        check();
        return jedis.lpush(key, value);
    }

    public static Long llen(String key) {
        check();
        return jedis.llen(key);
    }

    public static void hmset(String prefix, String key,String value) {
        check();
        ImmutableMap<String, String> of = ImmutableMap.of(key, value);
        jedis.hmset(prefix, of);
    }

    public static int hmget(String key, String field) {
        check();
        //todo
        String value = jedis.hmget(key, field).get(0);
        return "nil".equals(value)?0:Integer.parseInt(value);
    }

}
