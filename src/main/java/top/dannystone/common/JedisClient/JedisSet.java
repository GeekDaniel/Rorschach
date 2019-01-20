package top.dannystone.common.JedisClient;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2019-01-08
 * Time: 下午1:49
 */
public class JedisSet implements Set {

    public JedisSet(String setName) {
        this.setName = setName;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }


    private String setName;

    private JedisClient jedisClient = new JedisClient();

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        return jedisClient.smembers(setName).contains(JSONObject.toJSONString(o));
    }

    @NotNull
    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Object o) {
        return jedisClient.sadd(setName, JSONObject.toJSONString(o));
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection c) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Object[] toArray(@NotNull Object[] a) {
        throw new UnsupportedOperationException();
    }
}
