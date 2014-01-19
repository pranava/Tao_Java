package org.des.tao.ide.editors;

import com.google.common.collect.Maps;
import net.miginfocom.swing.MigLayout;
import org.des.tao.ide.resources.Colors;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
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

public class EdeEditor extends Editor {
    private static Pattern PARAMETER_REGEX = Pattern.compile(
            "(?s)\\s*\\(\\s*(.*)\\s*\\)\\s*");

    private transient EnumMap<EdgeType, NestedEditor> editorMap;

    private EdgeType edgeType = EdgeType.SCHEDULING;
    private String description = "Provide your edge description here.";
    private String condition = "(true)";
    private String priority = "(5)";


    private JLabel edgeTypeLabel;
    private JLabel descriptionLabel;
    private JLabel conditionLabel;
    private JLabel priorityLabel;

    private JComboBox edgeTypeComboBox;
    private JScrollPane descriptionScrollPane;
    private JTextArea descriptionTextArea;
    private JScrollPane priorityScrollPane;
    private JEditorPane priorityEditorPane;
    private JScrollPane conditionScrollPane;
    private JEditorPane conditionEditorPane;

    private JPanel nestedContentPanel;

    public EdeEditor() {
        super();

        editorMap = Maps.newEnumMap(EdgeType.class);
        editorMap.put(EdgeType.SCHEDULING, new SchedulingEdgeEditor());
        editorMap.put(EdgeType.PENDING, new PendingEdgeEditor());
        editorMap.put(EdgeType.CANCELLING, new CancellingEdgeEditor());

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
        //return delayEditorPane.getText();
        return "";
    }

    public String getPriority() {
        return priorityEditorPane.getText();
    }

    public String getParameters() {
        //Matcher parameterMatcher = PARAMETER_REGEX.matcher(
        //        parameterEditorPane.getText());
        //if (parameterMatcher.find())
        //    return parameterMatcher.group(1).trim();
        return "";
    }

    @Override
    public void commitChanges() {
        edgeType = (EdgeType) edgeTypeComboBox.getSelectedItem();
        description = descriptionTextArea.getText();
        condition = conditionEditorPane.getText();
        priority = priorityEditorPane.getText();
    }

    @Override
    public void revertChanges() {
        edgeTypeComboBox.setSelectedItem(edgeType);
        descriptionTextArea.setText(description);
        conditionEditorPane.setText(condition);
        priorityEditorPane.setText(priority);
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
                setNestedContent(
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

        conditionLabel = new JLabel("Condition: ");
        contentPanel.add(conditionLabel, "top, right");

        conditionEditorPane = new JEditorPane();
        conditionScrollPane = new JScrollPane(conditionEditorPane);
        conditionScrollPane.doLayout();

        conditionEditorPane.setContentType("text/java");
        contentPanel.add(conditionScrollPane, "span, width 200:100%:, height 50:60%:");

        priorityLabel = new JLabel("Priority: ");
        contentPanel.add(priorityLabel, "top, right");

        priorityEditorPane = new JEditorPane();
        priorityScrollPane = new JScrollPane(priorityEditorPane);
        priorityScrollPane.doLayout();

        priorityEditorPane.setContentType("text/java");
        contentPanel.add(priorityScrollPane, "span, width 200:100%:, height 50:");

        nestedContentPanel = new JPanel(new MigLayout());
        contentPanel.add(nestedContentPanel, "wrap");
    }

    private void setNestedContent(EdgeType edgeType) {
        NestedEditor nestedEditor = editorMap.get(edgeType);
        nestedContentPanel.removeAll();

        nestedEditor.drawNestedContentPanel(nestedContentPanel);
        //pack();
    }

    private class SchedulingEdgeEditor extends NestedEditor {
        private String delay = "(0)";
        private String parameters = "()";

        private JLabel delayLabel;
        private JLabel parameterLabel;

        private JScrollPane delayScrollPane;
        private JEditorPane delayEditorPane;
        private JScrollPane parameterScrollPane;
        private JEditorPane parameterEditorPane;

        @Override
        public void commitChanges() {
            delay = delayEditorPane.getText();
            parameters = parameterEditorPane.getText();
        }

        @Override
        public void revertChanges() {
            delayEditorPane.setText(delay);
            parameterEditorPane.setText(parameters);
        }

        @Override
        public void initialize() {
            delayLabel = new JLabel("Delay: ");

            delayEditorPane = new JEditorPane();
            delayScrollPane = new JScrollPane(delayEditorPane);
            delayScrollPane.doLayout();
            delayEditorPane.setContentType("text/java");

            parameterLabel = new JLabel("Parameter: ");

            parameterEditorPane = new JEditorPane();
            parameterScrollPane = new JScrollPane(parameterEditorPane);
            parameterScrollPane.doLayout();
            parameterEditorPane.setContentType("text/java");
        }

        @Override
        public void drawNestedContentPanel(JPanel nestedContentPanel) {
            nestedContentPanel.add(delayLabel, "top, right");
            nestedContentPanel.add(delayScrollPane, "span, width 200:100%:, height 50:");
            nestedContentPanel.add(parameterLabel, "top, right");
            nestedContentPanel.add(parameterScrollPane, "span, width 200:100%:, height 50:40%:");
        }
    }

    private class PendingEdgeEditor extends NestedEditor {
        private String parameters = "()";

        private JLabel parameterLabel;

        private JScrollPane parameterScrollPane;
        private JEditorPane parameterEditorPane;

        @Override
        public void commitChanges() {
            parameters = parameterEditorPane.getText();
        }

        @Override
        public void revertChanges() {
            parameterEditorPane.setText(parameters);
        }

        @Override
        public void initialize() {
            parameterLabel = new JLabel("Parameter: ");

            parameterEditorPane = new JEditorPane();
            parameterScrollPane = new JScrollPane(parameterEditorPane);
            parameterScrollPane.doLayout();
            parameterEditorPane.setContentType("text/java");
        }

        @Override
        public void drawNestedContentPanel(JPanel nestedContentPanel) {
            nestedContentPanel.add(parameterLabel, "top, right");
            nestedContentPanel.add(parameterScrollPane, "span, width 200:100%:, height 50:40%:");
        }
    }

    private class CancellingEdgeEditor extends NestedEditor {
        private String delay = "(0)";

        private JLabel delayLabel;

        private JScrollPane delayScrollPane;
        private JEditorPane delayEditorPane;

        @Override
        public void commitChanges() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void revertChanges() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void initialize() {
            edgeTypeLabel = new JLabel("Scope: ");
            edgeTypeComboBox = new JComboBox(EdgeType.values());

            delayLabel = new JLabel("Delay: ");

            delayEditorPane = new JEditorPane();
            delayScrollPane = new JScrollPane(delayEditorPane);
            delayScrollPane.doLayout();
            delayEditorPane.setContentType("text/java");
        }

        @Override
        public void drawNestedContentPanel(JPanel nestedContentPanel) {
            nestedContentPanel.add(edgeTypeLabel, "right");
            nestedContentPanel.add(edgeTypeComboBox, "span");
        }
    }
}
