package com.reqdrop.core;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobCompilerTest {

    private static boolean matches(String glob, boolean ci, String input) {
        Pattern p = GlobCompiler.compileGlob(glob, ci);
        return p.matcher(input).matches();
    }

    @Test
    void starMatchesSubdomainsButNotApexOrSuffix() {
        assertTrue(matches("*.example.com", true, "a.example.com"));
        assertTrue(matches("*.example.com", true, "x.y.example.com"));
        assertFalse(matches("*.example.com", true, "example.com"));
        assertFalse(matches("*.example.com", true, "evilexample.com"));
    }

    @Test
    void caseInsensitiveFlagAppliesToHost() {
        assertTrue(matches("*.example.com", true, "A.EXAMPLE.COM"));
        assertFalse(matches("*.example.com", false, "A.EXAMPLE.COM"));
    }

    @Test
    void pathStarMatchesChildrenNotBareSegment() {
        assertTrue(matches("/analytics/*", false, "/analytics/x"));
        assertTrue(matches("/analytics/*", false, "/analytics/"));
        assertFalse(matches("/analytics/*", false, "/analytics"));
    }

    @Test
    void metacharactersAreEscapedAndQuestionMarkMatchesOneChar() {
        assertTrue(matches("a.b", false, "a.b"));
        assertFalse(matches("a.b", false, "axb"));
        assertTrue(matches("f?o", false, "foo"));
        assertFalse(matches("f?o", false, "fo"));
    }
}
