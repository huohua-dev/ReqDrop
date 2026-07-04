package com.reqdrop.ui;

import com.reqdrop.core.RuleStore;
import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleTableModelTest {

    @Test
    void exposesRuleFieldsAsColumns() {
        RuleStore store = new RuleStore();
        store.addRule(new DropRule("a", true, "*.example.com", "/p/*", MatchMode.WILDCARD, "note"));
        RuleTableModel model = new RuleTableModel(store);

        assertEquals(1, model.getRowCount());
        assertEquals(6, model.getColumnCount());
        assertEquals(Boolean.TRUE, model.getValueAt(0, 0));
        assertEquals("*.example.com", model.getValueAt(0, 1));
        assertEquals("/p/*", model.getValueAt(0, 2));
        assertEquals("WILDCARD", model.getValueAt(0, 3));
        assertEquals("note", model.getValueAt(0, 4));
    }

    @Test
    void editingEnabledColumnUpdatesStore() {
        RuleStore store = new RuleStore();
        store.addRule(new DropRule("a", true, "*.example.com", "", MatchMode.WILDCARD, ""));
        RuleTableModel model = new RuleTableModel(store);

        assertTrue(model.isCellEditable(0, 0));
        model.setValueAt(Boolean.FALSE, 0, 0);
        model.refresh();
        assertFalse((Boolean) model.getValueAt(0, 0));
        assertFalse(store.rules().get(0).enabled());
    }
}
