package com.reqdrop.core;

import com.reqdrop.model.DropLogEntry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Bounded, thread-safe ring buffer of dropped-request log entries. */
public final class DropLog {

    private final int capacity;
    private final Deque<DropLogEntry> entries = new ArrayDeque<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public DropLog(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void add(DropLogEntry entry) {
        entries.addLast(entry);
        while (entries.size() > capacity) {
            entries.removeFirst();
        }
        notifyListeners();
    }

    public synchronized List<DropLogEntry> snapshot() {
        return new ArrayList<>(entries);
    }

    public synchronized void clear() {
        entries.clear();
        notifyListeners();
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
