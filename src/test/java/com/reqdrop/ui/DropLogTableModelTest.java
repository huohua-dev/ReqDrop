package com.reqdrop.ui;

import com.reqdrop.core.DropLog;
import com.reqdrop.model.DropLogEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DropLogTableModelTest {

    @Test
    void exposesEntryFieldsAfterRefresh() {
        DropLog log = new DropLog(10);
        DropLogTableModel model = new DropLogTableModel(log);

        log.add(new DropLogEntry(0L, "GET", "a.example.com", "http://a.example.com/x", "host=*.example.com"));
        model.refresh();

        assertEquals(1, model.getRowCount());
        assertEquals(5, model.getColumnCount());
        assertEquals("GET", model.getValueAt(0, 1));
        assertEquals("a.example.com", model.getValueAt(0, 2));
        assertEquals("http://a.example.com/x", model.getValueAt(0, 3));
        assertEquals("host=*.example.com", model.getValueAt(0, 4));
    }
}
