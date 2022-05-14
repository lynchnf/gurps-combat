package norman.gurps.gui.battle;

import norman.gurps.gui.ButtonDescriptor;
import norman.gurps.model.battle.Battle;
import norman.gurps.model.battle.BattleAction;
import norman.gurps.model.battle.BattleStage;
import norman.gurps.model.battle.CombatantShield;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CombatantTableModel extends AbstractTableModel {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatantTableModel.class);
    private ResourceBundle bundle = ResourceBundle.getBundle("message");
    private String[] columnNames;
    private List<CombatantTableRow> dataList = new ArrayList<>();
    private Battle battle;

    public CombatantTableModel(Battle battle) {
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
            return row.getBasicSpeed();
        } else if (columnIndex == 7) {
            return row.getDamageResistance();
        } else if (columnIndex == 8) {
            return row.getEncumbrance();
        } else if (columnIndex == 9) {
            return row.getHitPoints();
        } else if (columnIndex == 10) {
            return row.getCurrentHitPoints();
        } else if (columnIndex == 11) {
            return row.getCombatantWeapons().get(0);
        } else if (columnIndex == 12) {
            return row.getReadyShield();
        } else if (columnIndex == 13) {
            return row.getLastAction();
        } else {
            LOGGER.warn("Invalid columnIndex=\"" + columnIndex + "\"");
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Before battle starts, only the button and the columns between Strength and Current Hit Points (inclusive) can be changed.
        if (!battle.getStarted()) {
            return columnIndex == 0 || columnIndex >= 2 && columnIndex <= 10 || columnIndex == 12;
        } else {
            // After battle starts, ...
            return false;
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
            fireTableCellUpdated(rowIndex, 8); // Encumbrance
            fireTableCellUpdated(rowIndex, 9); // Hit Points
        } else if (columnIndex == 3) {
            row.setDexterity((Integer) aValue);
            fireTableCellUpdated(rowIndex, 3);
            fireTableCellUpdated(rowIndex, 6); // Basic Speed
        } else if (columnIndex == 4) {
            row.setIntelligence((Integer) aValue);
            fireTableCellUpdated(rowIndex, 4);
        } else if (columnIndex == 5) {
            row.setHealth((Integer) aValue);
            fireTableCellUpdated(rowIndex, 5);
            fireTableCellUpdated(rowIndex, 6); // Basic Speed
        } else if (columnIndex == 6) {
            row.setBasicSpeed((Double) aValue);
            fireTableCellUpdated(rowIndex, 6);
        } else if (columnIndex == 7) {
            row.setDamageResistance((Integer) aValue);
            fireTableCellUpdated(rowIndex, 7);
        } else if (columnIndex == 8) {
            row.setEncumbrance((Integer) aValue);
            fireTableCellUpdated(rowIndex, 8);
        } else if (columnIndex == 9) {
            row.setHitPoints((Integer) aValue);
            fireTableCellUpdated(rowIndex, 9);
        } else if (columnIndex == 10) {
            row.setCurrentHitPoints((Integer) aValue);
            fireTableCellUpdated(rowIndex, 10);
        } else if (columnIndex == 11) {
            //row.setReadyWeapon(aValue);
            fireTableCellUpdated(rowIndex, 11);
        } else if (columnIndex == 12) {
            row.setReadyShield((CombatantShield) aValue);
            fireTableCellUpdated(rowIndex, 12);
        } else if (columnIndex == 13) {
            row.setLastAction((BattleAction) aValue);
            fireTableCellUpdated(rowIndex, 13);
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

    public List<CombatantTableRow> getDataList() {
        return dataList;
    }

    public boolean isHiLited(int rowIndex, int columnIndex) {
        boolean battleIsStarted = battle.getStarted();
        boolean rowIsCurrent = dataList.get(rowIndex).getCurrentCombatant();
        boolean stageIsAction = battle.getStage() != null && battle.getStage().equals(BattleStage.CHOSE_ACTION);
        boolean columnIsAction = columnIndex == 13;
        return battleIsStarted && rowIsCurrent && stageIsAction && columnIsAction;
    }

    public void sortDataAndDisableRemove() {
        dataList.sort(
                Comparator.comparing(CombatantTableRow::getBasicSpeed).thenComparing(CombatantTableRow::getDexterity)
                        .reversed());
        for (CombatantTableRow row : dataList) {
            row.getButtonDescriptor().setEnabled(false);
        }
        fireTableDataChanged();
    }
}
