package de.orbit.ToB.arena.validator;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *    The DataContainer can store references to as many objects as possible. This makes it easier to provide a various
 *    amount of data and type of data to methods.
 * </p>
 */
public class DataContainer {

    private Map<String, Object> container = new HashMap<>();

    public DataContainer() {}

    public <T> void add(String key, T object) {
        this.container.put(key, object);
    }

    public <T> T get(String key) {
        return (T) this.container.get(key);
    }

}
