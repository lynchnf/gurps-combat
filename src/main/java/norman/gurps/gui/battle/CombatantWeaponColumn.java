package norman.gurps.gui.battle;

import norman.gurps.model.battle.CombatantWeapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.util.List;

public class CombatantWeaponColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatantWeaponColumn.class);
    private JTextField field = new JTextField();
    private JComboBox<CombatantWeapon> comboBox = new JComboBox<>();

    public CombatantWeaponColumn() {
        field.setEditable(false);
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedIndex() - 1;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        int index = (int) value;
        if (index < 0) {
            field.setText(null);
        } else {
            CombatantTableModel model = (CombatantTableModel) table.getModel();
            CombatantTableRow modelRow = model.getDataList().get(row);
            List<CombatantWeapon> weapons = modelRow.combatantWeapons();
            CombatantWeapon weapon = weapons.get(index);
            field.setText(weapon.toString());
        }
        return field;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.removeAllItems();
        comboBox.addItem(null);
        CombatantTableModel model = (CombatantTableModel) table.getModel();
        CombatantTableRow modelRow = model.getDataList().get(row);
        List<CombatantWeapon> weapons = modelRow.combatantWeapons();
        for (CombatantWeapon weapon : weapons) {
            comboBox.addItem(weapon);
        }
        int index = (int) value;
        comboBox.setSelectedIndex(index + 1);
        return comboBox;
    }
}
