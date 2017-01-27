package de.orbit.ToB.arena.validator;

import java.util.HashMap;
import java.util.Map;

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
