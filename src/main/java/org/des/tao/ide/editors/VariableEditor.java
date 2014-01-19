package org.des.tao.ide.editors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class VariableEditor extends Editor {
    public enum VariableColumn {
        TYPE("Type"),
        NAME("Name"),
        DESCRIPTION("Description");

        private final String displayName;

        private VariableColumn(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private int backupVariableCount;
    private List<String[]> backupTableModel;

    private int totalVariableCount;
    private JScrollPane tableScrollPane;
    private JTable variableEditorTable;
    private JButton addVariableButton;
    private JButton removeSelectedVariablesButton;
    private DefaultTableModel defaultTableModel;


    public VariableEditor() {
        super();

        totalVariableCount = 0;
        backupVariableCount = 0;
        backupTableModel = Lists.newLinkedList();
    }

    @Override
    public void commitChanges() {
        backupTableModel.clear();
        backupVariableCount = totalVariableCount;

        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            String[] rowValues = new String[defaultTableModel.getColumnCount()];
            for (int j = 0; j < rowValues.length; j++) {
                rowValues[j] = defaultTableModel.getValueAt(i, j).toString();
            }
            backupTableModel.add(rowValues);
        }
    }

    @Override
    public void revertChanges() {
        clearTableModel();
        for (String[] rowValues : backupTableModel) {
            defaultTableModel.addRow(rowValues);
        }

        totalVariableCount = backupVariableCount;
    }

    @Override
    public void initialize() {
        setTitle("Variable Editor");

        variableEditorTable = new JTable();
        tableScrollPane = new JScrollPane(variableEditorTable);
        variableEditorTable.setFillsViewportHeight(true);

        addVariableButton = new JButton("Add Variable");
        removeSelectedVariablesButton = new JButton("Remove Selected");
        defaultTableModel = new DefaultTableModel(
                VariableColumn.values(), 0);

        variableEditorTable.setModel(defaultTableModel);

        addVariableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                totalVariableCount += 1;
                defaultTableModel.addRow(new Object[]{
                        "Object",
                        "variable" + totalVariableCount,
                        "Add description."
                });
            }
        });

        removeSelectedVariablesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Integer> selectedRows = Ints.asList(
                        variableEditorTable.getSelectedRows());
                Collections.sort(selectedRows, Collections.reverseOrder());
                if (selectedRows.size() == 0) return;

                int firstIndex = selectedRows.get(selectedRows.size() - 1);
                for (int selectedRowIndex : selectedRows) {
                    defaultTableModel.removeRow(selectedRowIndex);
                }

                firstIndex = Math.min(
                        firstIndex, defaultTableModel.getRowCount() - 1);
                if (firstIndex != -1) {
                    variableEditorTable.setRowSelectionInterval(firstIndex, firstIndex);
                }
            }
        });

        contentPanel.add(tableScrollPane, "span, width 100%");
        contentPanel.add(addVariableButton);
        contentPanel.add(removeSelectedVariablesButton, "wrap, align right");
        contentPanel.setPreferredSize(new Dimension(600, 400));
    }

    public List<Map<String, Object>> getVariableList() {
        List<Map<String, Object>> variableList = Lists.newArrayList();
        for (String[] rowValues : backupTableModel) {
            Map<String, Object> variableProperties = Maps.newHashMap();
            variableProperties.put("type", rowValues[VariableColumn.TYPE.ordinal()]);
            variableProperties.put("name", rowValues[VariableColumn.NAME.ordinal()]);
            variableProperties.put("description", rowValues[VariableColumn.DESCRIPTION.ordinal()]);

            variableList.add(variableProperties);
        }

        return variableList;
    }

    private void clearTableModel() {
        defaultTableModel = new DefaultTableModel(
                VariableColumn.values(), 0);

        variableEditorTable.setModel(defaultTableModel);
    }
}
