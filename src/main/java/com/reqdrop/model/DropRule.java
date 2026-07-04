package com.reqdrop.model;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record DropRule(String id, boolean enabled, String hostPattern,
                       String pathPattern, MatchMode matchMode, String comment) {

    public DropRule {
        hostPattern = hostPattern == null ? "" : hostPattern;
        pathPattern = pathPattern == null ? "" : pathPattern;
        comment = comment == null ? "" : comment;
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    public DropRule withEnabled(boolean newEnabled) {
        return new DropRule(id, newEnabled, hostPattern, pathPattern, matchMode, comment);
    }

    public String displayLabel() {
        StringBuilder sb = new StringBuilder();
        if (!hostPattern.isEmpty()) {
            sb.append("host=").append(hostPattern);
        }
        if (!pathPattern.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append("path=").append(pathPattern);
        }
        return sb.toString();
    }

    /** @return empty if valid, otherwise a human-readable error message. */
    public static Optional<String> validate(String hostPattern, String pathPattern, MatchMode mode) {
        String host = hostPattern == null ? "" : hostPattern.trim();
        String path = pathPattern == null ? "" : pathPattern.trim();
        if (host.isEmpty() && path.isEmpty()) {
            return Optional.of("At least one of Host or Path pattern is required.");
        }
        if (mode == MatchMode.REGEX) {
            Optional<String> hostErr = compileError("Host", host);
            if (hostErr.isPresent()) {
                return hostErr;
            }
            Optional<String> pathErr = compileError("Path", path);
            if (pathErr.isPresent()) {
                return pathErr;
            }
        }
        return Optional.empty();
    }

    private static Optional<String> compileError(String field, String pattern) {
        if (pattern.isEmpty()) {
            return Optional.empty();
        }
        try {
            Pattern.compile(pattern);
            return Optional.empty();
        } catch (PatternSyntaxException e) {
            return Optional.of(field + " regex is invalid: " + e.getDescription());
        }
    }
}
