package org.des.tao.ide.editors;

import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public abstract class Editor extends JFrame implements Versionable {

    private Dimension dimension;
    private transient JPanel editorPanel;
    private transient JPanel buttonPanel;
    private transient JButton okButton;
    private transient JButton cancelButton;

    protected transient JPanel contentPanel;

    public Editor() {
        super();

        dimension = null;
        editorPanel = new JPanel(new MigLayout());
        contentPanel = new JPanel(new MigLayout());
        buttonPanel = new JPanel(new MigLayout());

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        configureListeners();
        initialize();

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        editorPanel.add(contentPanel, "wrap");
        editorPanel.add(buttonPanel, "span, align right");

        add(editorPanel);
    }

    public void commitCallback() {}
    public void revertCallback() {}

    final public void commit() {
        commitChanges();
        commitCallback();
    }

    final public void revert() {
        revertChanges();
        revertCallback();
    }

    public Dimension getDimension() {
        return dimension;
    }

    private void configureListeners() {
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dimension = getSize();
                commit();
                setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dimension = getSize();
                revert();
                setVisible(false);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                dimension = getSize();
                revert();
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
        getRootPane().getActionMap().put("Escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dimension = getSize();
                revert();
                setVisible(false);
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Commit");
        getRootPane().getActionMap().put("Commit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dimension = getSize();
                commit();
                setVisible(false);
            }
        });
    }

    protected abstract class NestedEditor implements Versionable {
        public NestedEditor() {
            initialize();
        }

        public abstract void drawNestedContentPanel(JPanel nestedContentPanel);
    }
}
