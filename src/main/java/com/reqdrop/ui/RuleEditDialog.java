package com.reqdrop.ui;

import com.reqdrop.model.DropRule;
import com.reqdrop.model.MatchMode;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;

public final class RuleEditDialog {

    private RuleEditDialog() {
    }

    public static Optional<DropRule> showDialog(Component parent, DropRule existing) {
        boolean isEdit = existing != null;
        JTextField host = new JTextField(isEdit ? existing.hostPattern() : "", 24);
        JTextField path = new JTextField(isEdit ? existing.pathPattern() : "", 24);
        JTextField comment = new JTextField(isEdit ? existing.comment() : "", 24);

        JRadioButton wildcard = new JRadioButton("Wildcard", !isEdit || existing.matchMode() == MatchMode.WILDCARD);
        JRadioButton regex = new JRadioButton("Regex", isEdit && existing.matchMode() == MatchMode.REGEX);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(wildcard);
        modeGroup.add(regex);
        JPanel modePanel = new JPanel();
        modePanel.add(wildcard);
        modePanel.add(regex);

        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, 0, "Host pattern:", host);
        addRow(form, 1, "Path pattern:", path);
        addRow(form, 2, "Match mode:", modePanel);
        addRow(form, 3, "Comment:", comment);

        String title = isEdit ? "Edit rule" : "Add rule";
        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    parent, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }
            MatchMode mode = regex.isSelected() ? MatchMode.REGEX : MatchMode.WILDCARD;
            Optional<String> error = DropRule.validate(host.getText(), path.getText(), mode);
            if (error.isPresent()) {
                JOptionPane.showMessageDialog(parent, error.get(), "Invalid rule", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            String id = isEdit ? existing.id() : DropRule.newId();
            boolean enabled = !isEdit || existing.enabled();
            return Optional.of(new DropRule(
                    id, enabled, host.getText().trim(), path.getText().trim(), mode, comment.getText().trim()));
        }
    }

    private static void addRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(4, 4, 4, 8);
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.insets = new Insets(4, 0, 4, 4);
        panel.add(field, fieldConstraints);
    }
}
