package norman.gurps.gui;

import norman.gurps.model.BattleAction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CombatantTableModel extends AbstractTableModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatantTableModel.class);
    private final String[] columnNames;
    private final List<CombatantTableRow> dataList = new ArrayList<>();

    public CombatantTableModel() {
        ResourceBundle bundle = ResourceBundle.getBundle("message");
        String columnNameCsv = bundle.getString("combatant.table.column.names");
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
        CombatantTableRow row = dataList.get(rowIndex);
        if (columnIndex == 0) {
            return row.getButtonDescriptor();
        } else if (columnIndex == 1) {
            return row.getName();
        } else if (columnIndex == 2) {
            return row.getStrength();
        } else if (columnIndex == 3) {
            return row.getDexterity();
        } else if (columnIndex == 4) {
            return row.getIntelligence();
        } else if (columnIndex == 5) {
            return row.getHealth();
        } else if (columnIndex == 6) {
            return row.getBasicSpeed();
        } else if (columnIndex == 7) {
            return row.getAction();
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
        CombatantTableRow row = dataList.get(rowIndex);
        if (columnIndex == 0) {
            row.setButtonDescriptor((ButtonDescriptor) aValue);
        } else if (columnIndex == 1) {
            row.setName((String) aValue);
        } else if (columnIndex == 2) {
            row.setStrength((Integer) aValue);
        } else if (columnIndex == 3) {
            row.setDexterity((Integer) aValue);
        } else if (columnIndex == 4) {
            row.setIntelligence((Integer) aValue);
        } else if (columnIndex == 5) {
            row.setHealth((Integer) aValue);
        } else if (columnIndex == 6) {
            row.setBasicSpeed((Double) aValue);
        } else if (columnIndex == 7) {
            row.setAction((BattleAction) aValue);
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void addRow(CombatantTableRow dataRow) {
        int newRowIndex = dataList.size();
        dataList.add(dataRow);
        fireTableRowsInserted(newRowIndex, newRowIndex);
    }

    public void removeRow(int rowIndex) {
        dataList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void sort() {
        dataList.sort(
                Comparator.comparing(CombatantTableRow::getBasicSpeed).thenComparing(CombatantTableRow::getDexterity));
        fireTableDataChanged();
    }
}
