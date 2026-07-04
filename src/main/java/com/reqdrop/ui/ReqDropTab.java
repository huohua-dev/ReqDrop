package com.reqdrop.ui;

import com.reqdrop.core.DropLog;
import com.reqdrop.core.RuleStore;
import com.reqdrop.model.DropRule;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Optional;

public final class ReqDropTab {

    private final RuleStore store;
    private final DropLog dropLog;
    private final RuleTableModel ruleModel;
    private final DropLogTableModel logModel;
    private final JTable ruleTable;
    private final JPanel root;

    public ReqDropTab(RuleStore store, DropLog dropLog) {
        this.store = store;
        this.dropLog = dropLog;
        this.ruleModel = new RuleTableModel(store);
        this.logModel = new DropLogTableModel(dropLog);
        this.ruleTable = new JTable(ruleModel);
        this.root = new JPanel(new BorderLayout());

        JTable logTable = new JTable(logModel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildRulesPanel(), buildLogPanel(logTable));
        split.setResizeWeight(0.6);
        root.add(split, BorderLayout.CENTER);

        // Live refresh: drops arrive off the EDT, so marshal updates onto it.
        dropLog.addListener(() -> SwingUtilities.invokeLater(() -> {
            logModel.refresh();
            ruleModel.refresh(); // updates hit counts
        }));
        store.addChangeListener(() -> SwingUtilities.invokeLater(ruleModel::refresh));
    }

    public JComponent component() {
        return root;
    }

    private JPanel buildRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox masterToggle = new JCheckBox("Enable ReqDrop", store.isEnabled());
        masterToggle.addActionListener(e -> store.setEnabled(masterToggle.isSelected()));

        JButton add = new JButton("Add");
        add.addActionListener(e -> {
            Optional<DropRule> rule = RuleEditDialog.showDialog(root, null);
            rule.ifPresent(r -> {
                store.addRule(r);
                ruleModel.refresh();
            });
        });

        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> {
            int row = ruleTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            DropRule current = ruleModel.ruleAt(row);
            Optional<DropRule> updated = RuleEditDialog.showDialog(root, current);
            updated.ifPresent(r -> {
                store.updateRule(r);
                ruleModel.refresh();
            });
        });

        JButton delete = new JButton("Delete");
        delete.addActionListener(e -> {
            int row = ruleTable.getSelectedRow();
            if (row < 0) {
                return;
            }
            store.removeRule(ruleModel.ruleAt(row).id());
            ruleModel.refresh();
        });

        toolbar.add(masterToggle);
        toolbar.add(add);
        toolbar.add(edit);
        toolbar.add(delete);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(ruleTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLogPanel(JTable logTable) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(new JLabel("Dropped requests"));
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            dropLog.clear();
            logModel.refresh();
        });
        toolbar.add(clear);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(logTable), BorderLayout.CENTER);
        return panel;
    }
}
