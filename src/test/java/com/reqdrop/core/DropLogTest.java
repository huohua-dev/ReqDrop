package com.reqdrop.core;

import com.reqdrop.model.DropLogEntry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DropLogTest {

    private static DropLogEntry entry(String method) {
        return new DropLogEntry(1000L, method, "h", "http://h/", "host=h");
    }

    @Test
    void keepsOnlyNewestUpToCapacity() {
        DropLog log = new DropLog(2);
        log.add(entry("A"));
        log.add(entry("B"));
        log.add(entry("C"));
        assertEquals(2, log.snapshot().size());
        assertEquals("B", log.snapshot().get(0).method());
        assertEquals("C", log.snapshot().get(1).method());
    }

    @Test
    void clearEmptiesAndNotifies() {
        DropLog log = new DropLog(10);
        AtomicInteger notifications = new AtomicInteger();
        log.addListener(notifications::incrementAndGet);
        log.add(entry("A"));
        log.clear();
        assertEquals(0, log.snapshot().size());
        assertEquals(2, notifications.get()); // one for add, one for clear
    }
}
