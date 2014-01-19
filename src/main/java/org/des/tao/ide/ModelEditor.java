package org.des.tao.ide;

import com.google.gson.Gson;
import freemarker.template.TemplateException;
import org.des.tao.ide.builder.ModelBuilder;
import org.des.tao.ide.builder.ModelCompiler;
import org.des.tao.ide.editors.VariableEditor;
import org.des.tao.ide.resources.Colors;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class ModelEditor {
    private String modelName;
    private JFrame modelFrame;
    private EventRelationshipGraph erg;
    private VariableEditor variableEditor;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu modelMenu;
    private JMenu variableMenu;
    private JMenuItem newModelMenuItem;
    private JMenuItem saveModelMenuItem;
    private JMenuItem selectAllMenuItem;
    private JMenuItem deleteMenuItem;
    private JMenuItem compileModelMenuItem;
    private JMenuItem editorMenuItem;

    public ModelEditor() {
        this("Untitled");
    }

    public ModelEditor(String modelName) {
        this.modelName = modelName;
        this.modelFrame = new JFrame(modelName);
        this.modelFrame.setSize(500, 400);

        initialize();
    }

    private void initialize() {
        menuBar = new JMenuBar();
        variableEditor = new VariableEditor();

        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        modelMenu = new JMenu("Model");
        variableMenu = new JMenu("Variables");

        newModelMenuItem = new JMenuItem("New Model", KeyEvent.VK_N);
        newModelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveModelMenuItem = new JMenuItem("Save Model", KeyEvent.VK_S);
        saveModelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveModelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Gson gson = new Gson();
                gson.toJson(erg, System.out);
            }
        });

        fileMenu.add(newModelMenuItem);
        fileMenu.add(saveModelMenuItem);
        menuBar.add(fileMenu);

        deleteMenuItem = new JMenuItem("Delete");
        selectAllMenuItem = new JMenuItem("Select All",
                KeyEvent.VK_A);
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));

        selectAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                erg.selectAllComponents();
            }
        });

        deleteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                erg.removeSelectedComponents();
            }
        });

        editMenu.add(deleteMenuItem);
        editMenu.add(selectAllMenuItem);
        menuBar.add(editMenu);

        compileModelMenuItem = new JMenuItem("Compile", KeyEvent.VK_C);
        compileModelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        compileModelMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ModelBuilder modelBuilder = new ModelBuilder(
                        modelName, variableEditor.getVariableList(),
                        erg.getEvents(), erg.getAdjacencyList());
                try {
                    File modelCode = modelBuilder.exportCode();
                    ModelCompiler.compileModel(modelCode);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (TemplateException e1) {
                    e1.printStackTrace();
                }
            }
        });
        modelMenu.add(compileModelMenuItem);
        menuBar.add(modelMenu);

        editorMenuItem = new JMenuItem("Editor", KeyEvent.VK_E);
        editorMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editorMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dimension dimension = variableEditor.getDimension();
                variableEditor.revert();

                if (dimension != null) {
                    variableEditor.setPreferredSize(dimension);
                }
                variableEditor.pack();
                variableEditor.setVisible(true);
            }
        });
        variableMenu.add(editorMenuItem);
        menuBar.add(variableMenu);

        modelFrame.setJMenuBar(menuBar);

        erg = new EventRelationshipGraph();
        erg.setBackground(Colors.ERG_BACKGROUND);
        modelFrame.add(erg);
    }

    public JFrame getFrame() {
        return modelFrame;
    }
}