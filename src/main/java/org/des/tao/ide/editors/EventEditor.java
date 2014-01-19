package org.des.tao.ide.editors;

import org.des.tao.ide.components.Event.EventState;
import org.des.tao.ide.resources.Colors;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class EventEditor extends Editor {
    private final EventState eventState;

    private transient JLabel nameLabel;
    private transient JLabel traceEventLabel;
    private transient JLabel descriptionLabel;
    private transient JLabel functionLabel;

    private transient JTextField nameTextField;
    private transient JCheckBox traceEventCheckBox;
    private transient JScrollPane descriptionScrollPane;
    private transient JTextArea descriptionTextArea;
    private transient JScrollPane functionScrollPane;
    private transient JEditorPane functionEditorPane;

    public EventEditor(EventState name) {
        super();

        this.eventState = name;
        revertChanges();
    }


    @Override
    public void commitChanges() {
        eventState.setName(nameTextField.getText());
        eventState.setTraced(traceEventCheckBox.isSelected());
        eventState.setDescription(descriptionTextArea.getText());
        eventState.setFunctionBody(functionEditorPane.getText());
    }

    @Override
    public void revertChanges() {
        nameTextField.setText(eventState.getName());
        traceEventCheckBox.setSelected(eventState.isTraced());
        descriptionTextArea.setText(eventState.getDescription());
        functionEditorPane.setText(eventState.getFunctionBody());
    }

    @Override
    public void initialize() {
        setTitle("Edit Event");

        nameLabel = new JLabel("Name: ");
        contentPanel.add(nameLabel, "right");

        nameTextField = new JTextField(12);
        contentPanel.add(nameTextField);

        traceEventCheckBox = new JCheckBox();
        contentPanel.add(traceEventCheckBox);

        traceEventLabel = new JLabel("Trace Event");
        contentPanel.add(traceEventLabel, "wrap");

        descriptionLabel = new JLabel("Description: ");
        contentPanel.add(descriptionLabel, "top, right");

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setForeground(Colors.COMMENT_COLOR);

        descriptionScrollPane = new JScrollPane(descriptionTextArea);
        contentPanel.add(descriptionScrollPane, "span, width 200:100%:, height 50:");

        functionLabel = new JLabel("Function: ");
        contentPanel.add(functionLabel, "top, right");

        functionEditorPane = new JEditorPane();
        functionScrollPane = new JScrollPane(functionEditorPane);
        functionScrollPane.doLayout();

        functionEditorPane.setContentType("text/java");
        contentPanel.add(functionScrollPane, "span, width 200:100%:, height 200:100%:");
    }
}
