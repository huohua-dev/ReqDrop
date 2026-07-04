package com.reqdrop.ui;

import com.reqdrop.core.DropLog;
import com.reqdrop.model.DropLogEntry;

import javax.swing.table.AbstractTableModel;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class DropLogTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"Time", "Method", "Host", "URL", "Rule"};
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private final DropLog log;
    private List<DropLogEntry> view;

    public DropLogTableModel(DropLog log) {
        this.log = log;
        this.view = log.snapshot();
    }

    public void refresh() {
        this.view = log.snapshot();
        fireTableDataChanged();
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
    public Object getValueAt(int row, int column) {
        DropLogEntry entry = view.get(row);
        return switch (column) {
            case 0 -> TIME_FORMAT.format(Instant.ofEpochMilli(entry.timestampMillis()));
            case 1 -> entry.method();
            case 2 -> entry.host();
            case 3 -> entry.url();
            case 4 -> entry.ruleLabel();
            default -> "";
        };
    }
}
