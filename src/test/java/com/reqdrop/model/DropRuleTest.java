package com.reqdrop.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DropRuleTest {

    @Test
    void validateRejectsBothPatternsEmpty() {
        Optional<String> err = DropRule.validate("  ", "", MatchMode.WILDCARD);
        assertTrue(err.isPresent());
    }

    @Test
    void validateAcceptsSingleNonEmptyWildcard() {
        assertTrue(DropRule.validate("*.example.com", "", MatchMode.WILDCARD).isEmpty());
    }

    @Test
    void validateRejectsInvalidRegexButAcceptsValid() {
        assertTrue(DropRule.validate("[", "", MatchMode.REGEX).isPresent());
        assertTrue(DropRule.validate("^/api/.*$", "", MatchMode.REGEX).isEmpty());
    }

    @Test
    void nullFieldsNormalizedToEmptyAndLabelBuilt() {
        DropRule r = new DropRule("id1", true, "*.example.com", null, MatchMode.WILDCARD, null);
        assertEquals("", r.pathPattern());
        assertEquals("", r.comment());
        assertEquals("host=*.example.com", r.displayLabel());
    }

    @Test
    void withEnabledReturnsToggledCopy() {
        DropRule r = new DropRule("id1", true, "*.example.com", "", MatchMode.WILDCARD, "");
        DropRule off = r.withEnabled(false);
        assertFalse(off.enabled());
        assertTrue(r.enabled());
        assertEquals(r.id(), off.id());
    }
}
