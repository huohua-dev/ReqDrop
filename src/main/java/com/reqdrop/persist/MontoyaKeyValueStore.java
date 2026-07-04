package com.reqdrop.persist;

import burp.api.montoya.persistence.PersistedObject;

import java.util.Set;

/** KeyValueStore backed by Burp's project-scoped PersistedObject. */
public final class MontoyaKeyValueStore implements KeyValueStore {

    private final PersistedObject delegate;

    public MontoyaKeyValueStore(PersistedObject delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getString(String key) {
        return delegate.getString(key);
    }

    @Override
    public void putString(String key, String value) {
        delegate.setString(key, value);
    }

    @Override
    public Boolean getBoolean(String key) {
        return delegate.getBoolean(key);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        delegate.setBoolean(key, value);
    }

    @Override
    public Integer getInteger(String key) {
        return delegate.getInteger(key);
    }

    @Override
    public void putInteger(String key, int value) {
        delegate.setInteger(key, value);
    }

    @Override
    public KeyValueStore newChild() {
        return new MontoyaKeyValueStore(PersistedObject.persistedObject());
    }

    @Override
    public void putChild(String key, KeyValueStore child) {
        delegate.setChildObject(key, ((MontoyaKeyValueStore) child).delegate);
    }

    @Override
    public KeyValueStore getChild(String key) {
        PersistedObject child = delegate.getChildObject(key);
        return child == null ? null : new MontoyaKeyValueStore(child);
    }

    @Override
    public void deleteChild(String key) {
        delegate.deleteChildObject(key);
    }

    @Override
    public Set<String> childKeys() {
        return delegate.childObjectKeys();
    }
}
