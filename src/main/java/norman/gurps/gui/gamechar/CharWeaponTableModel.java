package norman.gurps.gui.gamechar;

import norman.gurps.gui.ButtonDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CharWeaponTableModel extends AbstractTableModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharWeaponTableModel.class);
    private final String[] columnNames;
    private final List<CharWeaponTableRow> dataList = new ArrayList<>();

    public CharWeaponTableModel() {
        ResourceBundle bundle = ResourceBundle.getBundle("message");
        String columnNameCsv = bundle.getString("char.weapon.table.column.names");
        columnNames = StringUtils.split(columnNameCsv, ',');
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (dataList.isEmpty()) {
            return Object.class;
        } else {
            Object dataCell = getValueAt(0, columnIndex);
            if (dataCell == null) {
                return Object.class;
            } else {
                return dataCell.getClass();
            }
        }
    }

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CharWeaponTableRow row = dataList.get(rowIndex);
        if (columnIndex == 0) {
            return row.getButtonDescriptor();
        } else if (columnIndex == 1) {
            return row.getWeaponName();
        } else if (columnIndex == 2) {
            return row.getSkillName();
        } else if (columnIndex == 3) {
            return row.getSkillLevel();
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CharWeaponTableRow row = dataList.get(rowIndex);
        if (columnIndex == 0) {
            row.setButtonDescriptor((ButtonDescriptor) aValue);
            fireTableCellUpdated(rowIndex, 0);
        } else if (columnIndex == 1) {
            row.setWeaponName((String) aValue);
            fireTableCellUpdated(rowIndex, 1);
        } else if (columnIndex == 2) {
            row.setSkillName((String) aValue);
            fireTableCellUpdated(rowIndex, 1);
        } else if (columnIndex == 3) {
            row.setSkillLevel((Integer) aValue);
            fireTableCellUpdated(rowIndex, 3);
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
        }
    }

    public void addRow(CharWeaponTableRow dataRow) {
        int newRowIndex = dataList.size();
        dataList.add(dataRow);
        fireTableRowsInserted(newRowIndex, newRowIndex);
    }

    public void removeRow(int rowIndex) {
        dataList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
