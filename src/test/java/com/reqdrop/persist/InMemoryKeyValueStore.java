package com.reqdrop.persist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** In-memory KeyValueStore used to unit-test RulePersistence. */
public final class InMemoryKeyValueStore implements KeyValueStore {

    private final Map<String, String> strings = new HashMap<>();
    private final Map<String, Boolean> booleans = new HashMap<>();
    private final Map<String, Integer> integers = new HashMap<>();
    private final Map<String, KeyValueStore> children = new LinkedHashMap<>();

    @Override
    public String getString(String key) {
        return strings.get(key);
    }

    @Override
    public void putString(String key, String value) {
        strings.put(key, value);
    }

    @Override
    public Boolean getBoolean(String key) {
        return booleans.get(key);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        booleans.put(key, value);
    }

    @Override
    public Integer getInteger(String key) {
        return integers.get(key);
    }

    @Override
    public void putInteger(String key, int value) {
        integers.put(key, value);
    }

    @Override
    public KeyValueStore newChild() {
        return new InMemoryKeyValueStore();
    }

    @Override
    public void putChild(String key, KeyValueStore child) {
        children.put(key, child);
    }

    @Override
    public KeyValueStore getChild(String key) {
        return children.get(key);
    }

    @Override
    public void deleteChild(String key) {
        children.remove(key);
    }

    @Override
    public Set<String> childKeys() {
        return new HashSet<>(children.keySet());
    }
}
