package com.reqdrop.persist;

import java.util.Set;

/** Minimal key/value + nested-child store; abstracts Montoya's PersistedObject for testability. */
public interface KeyValueStore {

    String getString(String key);

    void putString(String key, String value);

    Boolean getBoolean(String key);

    void putBoolean(String key, boolean value);

    Integer getInteger(String key);

    void putInteger(String key, int value);

    /** Creates a new detached child store (not yet attached). */
    KeyValueStore newChild();

    void putChild(String key, KeyValueStore child);

    KeyValueStore getChild(String key);

    void deleteChild(String key);

    Set<String> childKeys();
}
