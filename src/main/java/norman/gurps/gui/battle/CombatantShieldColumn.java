package norman.gurps.gui.battle;

import norman.gurps.model.battle.CombatantShield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class CombatantShieldColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatantShieldColumn.class);
    private JTextField field = new JTextField();
    private JComboBox<CombatantShield> comboBox = new JComboBox<>();

    public CombatantShieldColumn() {
        field.setEditable(false);
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedIndex() > 0;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        boolean ready = (boolean) value;
        if (ready) {
            CombatantTableModel model = (CombatantTableModel) table.getModel();
            CombatantTableRow modelRow = model.getDataList().get(row);
            CombatantShield shield = modelRow.combatantShield();
            field.setText(shield.toString());
        } else {
            field.setText(null);
        }
        return field;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.removeAllItems();
        comboBox.addItem(null);
        CombatantTableModel model = (CombatantTableModel) table.getModel();
        CombatantTableRow modelRow = model.getDataList().get(row);
        CombatantShield shield = modelRow.combatantShield();
        comboBox.addItem(shield);

        boolean ready = (boolean) value;
        if (ready) {
            comboBox.setSelectedIndex(0);
        } else {
            comboBox.setSelectedIndex(1);
        }
        return comboBox;
    }
}
