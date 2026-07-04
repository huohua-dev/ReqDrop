package com.reqdrop.persist;

import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;

import java.util.ArrayList;
import java.util.List;

/** Serializes/deserializes the rule list + master flag through a KeyValueStore. */
public final class RulePersistence {

    private static final String ENABLED_KEY = "enabled";
    private static final String COUNT_KEY = "ruleCount";
    private static final String RULE_PREFIX = "rule_";

    private RulePersistence() {
    }

    public record Loaded(boolean masterEnabled, List<DropRule> rules) {
    }

    public static void save(KeyValueStore root, boolean masterEnabled, List<DropRule> rules) {
        root.putBoolean(ENABLED_KEY, masterEnabled);
        for (String key : root.childKeys()) {
            if (key.startsWith(RULE_PREFIX)) {
                root.deleteChild(key);
            }
        }
        root.putInteger(COUNT_KEY, rules.size());
        for (int i = 0; i < rules.size(); i++) {
            DropRule rule = rules.get(i);
            KeyValueStore child = root.newChild();
            child.putString("id", rule.id());
            child.putBoolean("enabled", rule.enabled());
            child.putString("hostPattern", rule.hostPattern());
            child.putString("pathPattern", rule.pathPattern());
            child.putString("matchMode", rule.matchMode().name());
            child.putString("comment", rule.comment());
            root.putChild(RULE_PREFIX + i, child);
        }
    }

    public static Loaded load(KeyValueStore root) {
        Boolean enabled = root.getBoolean(ENABLED_KEY);
        boolean masterEnabled = enabled == null || enabled;

        List<DropRule> rules = new ArrayList<>();
        Integer count = root.getInteger(COUNT_KEY);
        if (count != null) {
            for (int i = 0; i < count; i++) {
                KeyValueStore child = root.getChild(RULE_PREFIX + i);
                if (child == null) {
                    continue;
                }
                String id = child.getString("id");
                if (id == null) {
                    continue;
                }
                Boolean ruleEnabled = child.getBoolean("enabled");
                rules.add(new DropRule(
                        id,
                        ruleEnabled != null && ruleEnabled,
                        nullToEmpty(child.getString("hostPattern")),
                        nullToEmpty(child.getString("pathPattern")),
                        parseMode(child.getString("matchMode")),
                        nullToEmpty(child.getString("comment"))));
            }
        }
        return new Loaded(masterEnabled, rules);
    }

    private static MatchMode parseMode(String value) {
        if (value == null) {
            return MatchMode.WILDCARD;
        }
        try {
            return MatchMode.valueOf(value);
        } catch (IllegalArgumentException e) {
            return MatchMode.WILDCARD;
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
