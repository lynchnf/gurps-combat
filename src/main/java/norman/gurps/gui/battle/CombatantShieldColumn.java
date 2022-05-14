package norman.gurps.gui.battle;

import norman.gurps.model.battle.CombatantShield;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class CombatantShieldColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private JTextField field = new JTextField();
    private JComboBox<CombatantShield> comboBox = new JComboBox<>();

    public CombatantShieldColumn() {
        field.setEditable(false);
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value == null) {
            field.setText(null);
        } else {
            CombatantTableModel model = (CombatantTableModel) table.getModel();
            CombatantTableRow modelRow = model.getDataList().get(row);
            field.setText(modelRow.combatantShield().toString());
        }
        return field;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.removeAllItems();
        comboBox.addItem(null);
        CombatantTableModel model = (CombatantTableModel) table.getModel();
        CombatantTableRow modelRow = model.getDataList().get(row);
        comboBox.addItem(modelRow.combatantShield());
        if (value == null) {
            comboBox.setSelectedIndex(0);
        } else {
            comboBox.setSelectedIndex(1);
        }
        return comboBox;
    }
}
