package org.des.tao.ide.editors;

import org.des.tao.ide.resources.Colors;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.des.tao.ide.components.Edge.EdgeType;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class EdgeEditor extends Editor {
    private static Pattern PARAMETER_REGEX = Pattern.compile(
            "(?s)\\s*\\(\\s*(.*)\\s*\\)\\s*");

    private EdgeType edgeType = EdgeType.SCHEDULING;
    private String description = "Provide your edge description here.";
    private String delay = "(0)";
    private String condition = "(true)";
    private String parameters = "()";
    private String priority = "(5)";

    private JLabel edgeTypeLabel;
    private JLabel descriptionLabel;
    private JLabel delayLabel;
    private JLabel conditionLabel;
    private JLabel parameterLabel;
    private JLabel priorityLabel;

    private JComboBox edgeTypeComboBox;
    private JScrollPane descriptionScrollPane;
    private JTextArea descriptionTextArea;
    private JScrollPane delayScrollPane;
    private JEditorPane delayEditorPane;
    private JScrollPane conditionScrollPane;
    private JEditorPane conditionEditorPane;
    private JScrollPane parameterScrollPane;
    private JEditorPane parameterEditorPane;
    private JScrollPane priorityScrollPane;
    private JEditorPane priorityEditorPane;

    public EdgeEditor() {
        super();
        revertChanges();
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public String getDescription() {
        return description;
    }

    public String getCondition() {
        return conditionEditorPane.getText();
    }

    public String getDelay() {
        return delayEditorPane.getText();
    }

    public String getPriority() {
        return priorityEditorPane.getText();
    }

    public String getParameters() {
        Matcher parameterMatcher = PARAMETER_REGEX.matcher(
                parameterEditorPane.getText());
        if (parameterMatcher.find())
            return parameterMatcher.group(1).trim();
        return "";
    }

    @Override
    public void commitChanges() {
        edgeType = (EdgeType) edgeTypeComboBox.getSelectedItem();
        description = descriptionTextArea.getText();
        delay = delayEditorPane.getText();
        condition = conditionEditorPane.getText();
        parameters = parameterEditorPane.getText();
        priority = priorityEditorPane.getText();
    }

    @Override
    public void revertChanges() {
        edgeTypeComboBox.setSelectedItem(edgeType);
        descriptionTextArea.setText(description);
        delayEditorPane.setText(delay);
        conditionEditorPane.setText(condition);
        parameterEditorPane.setText(parameters);
        priorityEditorPane.setText(priority);

        toggleDelayEditor(edgeType);
    }

    @Override
    public void initialize() {
        setTitle("Edit Edge");

        edgeTypeLabel = new JLabel("Type: ");
        contentPanel.add(edgeTypeLabel, "right");

        edgeTypeComboBox = new JComboBox(EdgeType.values());
        contentPanel.add(edgeTypeComboBox, "span");

        edgeTypeComboBox.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                toggleDelayEditor(
                        (EdgeType) edgeTypeComboBox.getSelectedItem());
            }
        });

        descriptionLabel = new JLabel("Description: ");
        contentPanel.add(descriptionLabel, "top, right");

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setForeground(Colors.COMMENT_COLOR);

        descriptionScrollPane = new JScrollPane(descriptionTextArea);
        contentPanel.add(descriptionScrollPane, "span, width 200:100%:, height 50:");

        delayLabel = new JLabel("Delay: ");
        contentPanel.add(delayLabel, "top, right");

        delayEditorPane = new JEditorPane();
        delayScrollPane = new JScrollPane(delayEditorPane);
        delayScrollPane.doLayout();

        delayEditorPane.setContentType("text/java");
        contentPanel.add(delayScrollPane, "span, width 200:100%:, height 50:");

        conditionLabel = new JLabel("Condition: ");
        contentPanel.add(conditionLabel, "top, right");

        conditionEditorPane = new JEditorPane();
        conditionScrollPane = new JScrollPane(conditionEditorPane);
        conditionScrollPane.doLayout();

        conditionEditorPane.setContentType("text/java");
        contentPanel.add(conditionScrollPane, "span, width 200:100%:, height 50:60%:");

        parameterLabel = new JLabel("Parameter: ");
        contentPanel.add(parameterLabel, "top, right");

        parameterEditorPane = new JEditorPane();
        parameterScrollPane = new JScrollPane(parameterEditorPane);
        parameterScrollPane.doLayout();

        parameterEditorPane.setContentType("text/java");
        contentPanel.add(parameterScrollPane, "span, width 200:100%:, height 50:40%:");

        priorityLabel = new JLabel("Priority: ");
        contentPanel.add(priorityLabel, "top, right");

        priorityEditorPane = new JEditorPane();
        priorityScrollPane = new JScrollPane(priorityEditorPane);
        priorityScrollPane.doLayout();

        priorityEditorPane.setContentType("text/java");
        contentPanel.add(priorityScrollPane, "span, width 200:100%:, height 50:");
    }

    private void toggleDelayEditor(EdgeType edgeType) {
        if (edgeType == EdgeType.PENDING) {
            delayEditorPane.setEditable(false);
            delayEditorPane.setVisible(false);
        } else {
            delayEditorPane.setEditable(true);
            delayEditorPane.setVisible(true);
        }
    }
}
