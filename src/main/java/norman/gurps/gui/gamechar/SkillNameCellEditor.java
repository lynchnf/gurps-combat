package norman.gurps.gui.gamechar;

import norman.gurps.model.equipment.WeaponSkill;
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

public class SkillNameCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillNameCellEditor.class);
    private final JComboBox<String> comboBox = new JComboBox<>();

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.removeAllItems();
        comboBox.addItem(null);
        CharWeaponTableModel model = (CharWeaponTableModel) table.getModel();
        String weaponName = (String) model.getValueAt(row, 1);
        List<WeaponSkill> skills = MeleeWeaponService.findWeaponSkills(weaponName);
        skills.sort(Comparator.comparing(WeaponSkill::getSkillName));
        for (WeaponSkill skill : skills) {
            comboBox.addItem(skill.getSkillName());
        }
        comboBox.setSelectedItem(value);
        return comboBox;
    }
}
