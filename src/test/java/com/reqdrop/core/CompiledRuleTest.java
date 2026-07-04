package com.reqdrop.core;

import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompiledRuleTest {

    private static CompiledRule rule(String host, String path, MatchMode mode) {
        return new CompiledRule(new DropRule("id", true, host, path, mode, ""));
    }

    @Test
    void hostOnlyWildcardIsCaseInsensitive() {
        CompiledRule r = rule("*.example.com", "", MatchMode.WILDCARD);
        assertTrue(r.matches("A.EXAMPLE.COM", "/anything"));
        assertFalse(r.matches("example.com", "/anything"));
    }

    @Test
    void pathOnlyWildcardIsCaseSensitive() {
        CompiledRule r = rule("", "/analytics/*", MatchMode.WILDCARD);
        assertTrue(r.matches("any.host", "/analytics/collect"));
        assertFalse(r.matches("any.host", "/Analytics/collect"));
    }

    @Test
    void hostAndPathAreAnded() {
        CompiledRule r = rule("*.example.com", "/analytics/*", MatchMode.WILDCARD);
        assertTrue(r.matches("a.example.com", "/analytics/x"));
        assertFalse(r.matches("a.example.com", "/other"));
        assertFalse(r.matches("other.org", "/analytics/x"));
    }

    @Test
    void regexUsesUnanchoredFind() {
        CompiledRule r = rule("", "/api/v[0-9]+/", MatchMode.REGEX);
        assertTrue(r.matches("h", "/prefix/api/v2/users"));
        assertFalse(r.matches("h", "/apix/users"));
    }

    @Test
    void bothPatternsEmptyMatchesNothing() {
        CompiledRule r = rule("", "", MatchMode.WILDCARD);
        assertFalse(r.matches("any.host", "/any"));
    }
}
