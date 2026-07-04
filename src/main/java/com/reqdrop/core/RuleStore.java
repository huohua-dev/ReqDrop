package com.reqdrop.core;

import com.reqdrop.model.DropRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Holds the rule list, master toggle, hit counters and a lock-free compiled
 * snapshot used by the proxy handler. Reads (firstMatch) are lock-free; writes
 * rebuild an immutable snapshot under a lock.
 */
public final class RuleStore {

    private final Consumer<String> errorLogger;
    private final Object lock = new Object();
    private volatile boolean enabled = true;
    private volatile List<CompiledRule> snapshot = List.of();
    private List<DropRule> rules = new ArrayList<>();               // guarded by lock
    private final Map<String, AtomicInteger> hits = new ConcurrentHashMap<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public RuleStore() {
        this(message -> {
        });
    }

    public RuleStore(Consumer<String> errorLogger) {
        this.errorLogger = errorLogger;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
        fireChange();
    }

    public List<DropRule> rules() {
        synchronized (lock) {
            return new ArrayList<>(rules);
        }
    }

    public void setRules(List<DropRule> newRules) {
        synchronized (lock) {
            this.rules = new ArrayList<>(newRules);
            rebuildSnapshot();
        }
        fireChange();
    }

    public void addRule(DropRule rule) {
        List<DropRule> copy = rules();
        copy.add(rule);
        setRules(copy);
    }

    public void updateRule(DropRule rule) {
        List<DropRule> copy = rules();
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).id().equals(rule.id())) {
                copy.set(i, rule);
                break;
            }
        }
        setRules(copy);
    }

    public void removeRule(String id) {
        List<DropRule> copy = rules();
        copy.removeIf(r -> r.id().equals(id));
        hits.remove(id);
        setRules(copy);
    }

    public void setRuleEnabled(String id, boolean ruleEnabled) {
        List<DropRule> copy = rules();
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).id().equals(id)) {
                copy.set(i, copy.get(i).withEnabled(ruleEnabled));
                break;
            }
        }
        setRules(copy);
    }

    // Called under lock.
    private void rebuildSnapshot() {
        List<CompiledRule> next = new ArrayList<>();
        for (DropRule rule : rules) {
            try {
                next.add(new CompiledRule(rule));
            } catch (RuntimeException e) {
                errorLogger.accept("ReqDrop: skipping invalid rule " + rule.displayLabel()
                        + ": " + e.getMessage());
            }
        }
        this.snapshot = List.copyOf(next);
    }

    public Optional<DropRule> firstMatch(String host, String path) {
        if (!enabled) {
            return Optional.empty();
        }
        for (CompiledRule compiled : snapshot) {
            if (!compiled.rule().enabled()) {
                continue;
            }
            if (compiled.matches(host, path)) {
                return Optional.of(compiled.rule());
            }
        }
        return Optional.empty();
    }

    public int hitCount(String id) {
        AtomicInteger counter = hits.get(id);
        return counter == null ? 0 : counter.get();
    }

    public void incrementHit(String id) {
        hits.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet();
    }

    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private void fireChange() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
