package com.reqdrop.persist;

import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RulePersistenceTest {

    @Test
    void roundTripsRulesAndMasterFlag() {
        InMemoryKeyValueStore root = new InMemoryKeyValueStore();
        List<DropRule> rules = List.of(
                new DropRule("a", true, "*.example.com", "", MatchMode.WILDCARD, "telemetry"),
                new DropRule("b", false, "", "/analytics/.*", MatchMode.REGEX, ""));

        RulePersistence.save(root, false, rules);
        RulePersistence.Loaded loaded = RulePersistence.load(root);

        assertFalse(loaded.masterEnabled());
        assertEquals(rules, loaded.rules());
    }

    @Test
    void defaultsWhenEmpty() {
        RulePersistence.Loaded loaded = RulePersistence.load(new InMemoryKeyValueStore());
        assertTrue(loaded.masterEnabled());
        assertTrue(loaded.rules().isEmpty());
    }

    @Test
    void saveClearsPreviousRuleChildren() {
        InMemoryKeyValueStore root = new InMemoryKeyValueStore();
        RulePersistence.save(root, true, List.of(
                new DropRule("a", true, "*.a.com", "", MatchMode.WILDCARD, ""),
                new DropRule("b", true, "*.b.com", "", MatchMode.WILDCARD, "")));
        RulePersistence.save(root, true, List.of(
                new DropRule("c", true, "*.c.com", "", MatchMode.WILDCARD, "")));

        RulePersistence.Loaded loaded = RulePersistence.load(root);
        assertEquals(1, loaded.rules().size());
        assertEquals("c", loaded.rules().get(0).id());
    }
}
