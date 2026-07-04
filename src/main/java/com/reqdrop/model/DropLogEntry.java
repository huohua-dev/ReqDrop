package com.reqdrop.model;

public record DropLogEntry(long timestampMillis, String method, String host,
                           String url, String ruleLabel) {
}
