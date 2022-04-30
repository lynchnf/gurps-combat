package foo.bar;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

public class JTableButtonModel extends AbstractTableModel {
    private Object[][] rows = {{"Button1", new JButton("Button1")}, {"Button2", new JButton("Button2")},
            {"Button3", new JButton("Button3")}, {"Button4", new JButton("Button4")}};
    private String[] columns = {"Count", "Buttons"};

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return rows.length;
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int row, int column) {
        return rows[row][column];
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Class getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }
}
