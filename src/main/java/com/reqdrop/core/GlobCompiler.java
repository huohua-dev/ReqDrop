package com.reqdrop.core;

import java.util.regex.Pattern;

/** Compiles a glob pattern ({@code *}, {@code ?}) into an anchored regex Pattern. */
public final class GlobCompiler {

    private static final String METACHARACTERS = ".^$+()[]{}|\\";

    private GlobCompiler() {
    }

    public static Pattern compileGlob(String glob, boolean caseInsensitive) {
        StringBuilder sb = new StringBuilder("^");
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            if (c == '*') {
                sb.append(".*");
            } else if (c == '?') {
                sb.append('.');
            } else if (METACHARACTERS.indexOf(c) >= 0) {
                sb.append('\\').append(c);
            } else {
                sb.append(c);
            }
        }
        sb.append('$');
        int flags = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
        return Pattern.compile(sb.toString(), flags);
    }
}
