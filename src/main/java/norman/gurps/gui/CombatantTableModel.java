package norman.gurps.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CombatantTableModel extends AbstractTableModel {
    private String[] columnNames;
    private List<Object[]> rowList = new ArrayList<>();

    public CombatantTableModel(String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return rowList.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object[] rowData = rowList.get(row);
        return rowData[col];
    }

    public void addRow(Object[] rowData) {
        rowList.add(rowData);
    }
}
