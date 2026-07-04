package com.reqdrop.core;

import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleStoreTest {

    private static DropRule host(String id, String host) {
        return new DropRule(id, true, host, "", MatchMode.WILDCARD, "");
    }

    @Test
    void firstMatchReturnsMatchingEnabledRule() {
        RuleStore store = new RuleStore();
        store.addRule(host("a", "*.example.com"));
        assertTrue(store.firstMatch("x.example.com", "/p").isPresent());
        assertFalse(store.firstMatch("other.org", "/p").isPresent());
    }

    @Test
    void masterToggleOffSuppressesAllMatches() {
        RuleStore store = new RuleStore();
        store.addRule(host("a", "*.example.com"));
        store.setEnabled(false);
        assertFalse(store.firstMatch("x.example.com", "/p").isPresent());
    }

    @Test
    void perRuleDisableIsSkipped() {
        RuleStore store = new RuleStore();
        store.addRule(host("a", "*.example.com"));
        store.setRuleEnabled("a", false);
        assertFalse(store.firstMatch("x.example.com", "/p").isPresent());
    }

    @Test
    void multipleRulesAreOred() {
        RuleStore store = new RuleStore();
        store.setRules(List.of(host("a", "*.a.com"), host("b", "*.b.com")));
        assertTrue(store.firstMatch("x.b.com", "/p").isPresent());
    }

    @Test
    void hitCountsAndChangeListener() {
        RuleStore store = new RuleStore();
        AtomicInteger changes = new AtomicInteger();
        store.addChangeListener(changes::incrementAndGet);
        store.addRule(host("a", "*.example.com"));
        store.incrementHit("a");
        store.incrementHit("a");
        assertEquals(2, store.hitCount("a"));
        assertEquals(1, changes.get()); // addRule fired once; incrementHit does not
    }
}
