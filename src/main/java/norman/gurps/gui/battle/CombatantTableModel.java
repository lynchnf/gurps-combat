package norman.gurps.gui.battle;

import norman.gurps.gui.ButtonDescriptor;
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
    private boolean started = false;

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
            return row.getHitPoints();
        } else if (columnIndex == 7) {
            return row.getBasicSpeed();
        } else if (columnIndex == 8) {
            return row.getDamageResistance();
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Before battle starts, name column may not be changed. After battle starts, only the button many be "changed".
        if (started) {
            return columnIndex == 0;
        } else {
            return columnIndex != 1;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CombatantTableRow row = dataList.get(rowIndex);
        if (columnIndex == 0) {
            row.setButtonDescriptor((ButtonDescriptor) aValue);
            fireTableCellUpdated(rowIndex, 0);
        } else if (columnIndex == 1) {
            row.setName((String) aValue);
            fireTableCellUpdated(rowIndex, 1);
        } else if (columnIndex == 2) {
            row.setStrength((Integer) aValue);
            fireTableCellUpdated(rowIndex, 2);
            fireTableCellUpdated(rowIndex, 6);
        } else if (columnIndex == 3) {
            row.setDexterity((Integer) aValue);
            fireTableCellUpdated(rowIndex, 3);
            fireTableCellUpdated(rowIndex, 7);
        } else if (columnIndex == 4) {
            row.setIntelligence((Integer) aValue);
            fireTableCellUpdated(rowIndex, 4);
        } else if (columnIndex == 5) {
            row.setHealth((Integer) aValue);
            fireTableCellUpdated(rowIndex, 5);
        } else if (columnIndex == 6) {
            row.setHitPoints((Integer) aValue);
            fireTableCellUpdated(rowIndex, 6);
            fireTableCellUpdated(rowIndex, 7);
        } else if (columnIndex == 7) {
            row.setBasicSpeed((Double) aValue);
            fireTableCellUpdated(rowIndex, 7);
        } else if (columnIndex == 8) {
            row.setDamageResistance((Integer) aValue);
            fireTableCellUpdated(rowIndex, 8);
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
        }
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

    public void start() {
        started = true;
        dataList.sort(
                Comparator.comparing(CombatantTableRow::getBasicSpeed).thenComparing(CombatantTableRow::getDexterity)
                        .reversed());
        fireTableDataChanged();
    }

    public boolean isStarted() {
        return started;
    }
}
