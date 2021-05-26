package org.smartregister.util;

import java.util.HashMap;
import java.util.Map;

public class EasyMap<KeyType, ValueType> {
    private Map<KeyType, ValueType> map;

    public EasyMap() {
        this.map = new HashMap<KeyType, ValueType>();
    }

    public static <Key, Value> Map<Key, Value> mapOf(Key key, Value value) {
        HashMap<Key, Value> normalMap = new HashMap<Key, Value>();
        normalMap.put(key, value);
        return normalMap;
    }

    public static <Key, Value> EasyMap<Key, Value> create(Key key, Value value) {
        EasyMap<Key, Value> easyMap = new EasyMap<Key, Value>();
        return easyMap.put(key, value);
    }

    public EasyMap<KeyType, ValueType> put(KeyType key, ValueType value) {
        map.put(key, value);
        return this;
    }

    public Map<KeyType, ValueType> map() {
        return map;
    }
}
