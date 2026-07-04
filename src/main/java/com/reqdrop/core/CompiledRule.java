package com.reqdrop.core;

import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;

import java.util.regex.Pattern;

/** A DropRule with its host/path patterns precompiled for fast matching. */
public final class CompiledRule {

    private final DropRule rule;
    private final Pattern hostPattern;   // null => no host constraint
    private final Pattern pathPattern;   // null => no path constraint
    private final boolean useFind;       // regex => find(); wildcard => matches()

    public CompiledRule(DropRule rule) {
        this.rule = rule;
        this.useFind = rule.matchMode() == MatchMode.REGEX;
        this.hostPattern = compile(rule.hostPattern(), rule.matchMode(), true);
        this.pathPattern = compile(rule.pathPattern(), rule.matchMode(), false);
    }

    private static Pattern compile(String pattern, MatchMode mode, boolean caseInsensitive) {
        if (pattern == null || pattern.isEmpty()) {
            return null;
        }
        if (mode == MatchMode.WILDCARD) {
            return GlobCompiler.compileGlob(pattern, caseInsensitive);
        }
        int flags = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
        return Pattern.compile(pattern, flags);
    }

    public boolean matches(String host, String path) {
        if (hostPattern == null && pathPattern == null) {
            return false;
        }
        if (hostPattern != null) {
            if (host == null || !test(hostPattern, host)) {
                return false;
            }
        }
        if (pathPattern != null) {
            if (path == null || !test(pathPattern, path)) {
                return false;
            }
        }
        return true;
    }

    private boolean test(Pattern p, String input) {
        return useFind ? p.matcher(input).find() : p.matcher(input).matches();
    }

    public DropRule rule() {
        return rule;
    }
}
