package norman.gurps.gui.battle;

import norman.gurps.gui.ButtonDescriptor;
import norman.gurps.model.battle.Battle;
import norman.gurps.model.battle.BattleAction;
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
    private final Battle battle;

    public CombatantTableModel(Battle battle) {
        ResourceBundle bundle = ResourceBundle.getBundle("message");
        String columnNameCsv = bundle.getString("combatant.table.column.names");
        columnNames = StringUtils.split(columnNameCsv, ',');
        this.battle = battle;
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
        } else if (columnIndex == 9) {
            return row.getEncumbrance();
        } else if (columnIndex == 10) {
            return row.getLastAction();
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Before battle starts, name and action columns may not be changed.
        if (!battle.isStarted()) {
            return columnIndex != 1 && columnIndex != 10;
        } else {
            // After battle starts, ...
            if (columnIndex == 0) {
                return true;
            } else {
                CombatantTableRow row = dataList.get(rowIndex);
                return row.getName().equals(battle.getCurrentCombatant());
            }
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
            fireTableCellUpdated(rowIndex, 9);
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
        } else if (columnIndex == 9) {
            row.setEncumbrance((Integer) aValue);
            fireTableCellUpdated(rowIndex, 9);
        } else if (columnIndex == 10) {
            row.setLastAction((BattleAction) aValue);
            fireTableCellUpdated(rowIndex, 10);
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
        String combatantToRemove = dataList.get(rowIndex).getName();
        if (battle.isStarted() && battle.getCurrentCombatant().equals(combatantToRemove)) {
            int nextRowIndex = (rowIndex + 1) % dataList.size();
            String nextCombatant = dataList.get(nextRowIndex).getName();
            battle.setCurrentCombatant(nextCombatant);
        }
        dataList.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void start() {
        battle.setStarted(true);
        dataList.sort(
                Comparator.comparing(CombatantTableRow::getBasicSpeed).thenComparing(CombatantTableRow::getDexterity)
                        .reversed());
        battle.setCurrentCombatant(dataList.get(0).getName());
        fireTableDataChanged();
    }

    public boolean isHiLited(int row, int column) {
        boolean battleIsStarted = battle.isStarted();
        String rowName = dataList.get(row).getName();
        boolean rowIsCurrent = rowName.equals(battle.getCurrentCombatant());
        boolean columnIsAction = column == 10;
        return battleIsStarted && rowIsCurrent && columnIsAction;
    }
}
