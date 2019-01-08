package top.dannystone.common.JedisClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2019-01-08
 * Time: 下午7:38
 */
public class UnSupportMap implements Map {
    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object key) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NotNull Map m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<Entry> entrySet() {
        throw new UnsupportedOperationException();
    }
}
