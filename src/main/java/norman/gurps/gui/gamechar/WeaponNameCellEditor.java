package norman.gurps.gui.gamechar;

import norman.gurps.model.equipment.MeleeWeapon;
import norman.gurps.service.MeleeWeaponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.Comparator;
import java.util.List;

public class WeaponNameCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(WeaponNameCellEditor.class);
    private JComboBox<String> comboBox = new JComboBox<>();

    public WeaponNameCellEditor() {
        comboBox.addItem(null);
        List<MeleeWeapon> weapons = MeleeWeaponService.findAll();
        weapons.sort(Comparator.comparing(MeleeWeapon::getName));
        for (MeleeWeapon weapon : weapons) {
            comboBox.addItem(weapon.getName());
        }
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.setSelectedItem(value);
        return comboBox;
    }
}
