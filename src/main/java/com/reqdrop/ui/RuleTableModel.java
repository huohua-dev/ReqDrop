package com.reqdrop.ui;

import com.reqdrop.core.RuleStore;
import com.reqdrop.model.DropRule;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public final class RuleTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"On", "Host", "Path", "Mode", "Comment", "Hits"};

    private final RuleStore store;
    private List<DropRule> view;

    public RuleTableModel(RuleStore store) {
        this.store = store;
        this.view = store.rules();
    }

    public void refresh() {
        this.view = store.rules();
        fireTableDataChanged();
    }

    public DropRule ruleAt(int row) {
        return view.get(row);
    }

    @Override
    public int getRowCount() {
        return view.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == 0 ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public Object getValueAt(int row, int column) {
        DropRule rule = view.get(row);
        return switch (column) {
            case 0 -> rule.enabled();
            case 1 -> rule.hostPattern();
            case 2 -> rule.pathPattern();
            case 3 -> rule.matchMode().name();
            case 4 -> rule.comment();
            case 5 -> String.valueOf(store.hitCount(rule.id()));
            default -> "";
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == 0) {
            store.setRuleEnabled(view.get(row).id(), Boolean.TRUE.equals(value));
        }
    }
}
