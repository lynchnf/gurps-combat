package norman.gurps.gui.battle;

import norman.gurps.model.battle.BattleAction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BattleActionColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleActionColumn.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle("message");
    private final JTextField field = new JTextField();
    private final Color defaultFieldBackground;
    private final JComboBox<String> comboBox = new JComboBox<>();
    private final Color defaultComboBoxBackground;
    private final Color hiliteBackground;
    private final Map<String, BattleAction> reverseLookup = new HashMap<>();

    public BattleActionColumn() {
        field.setEditable(false);
        defaultFieldBackground = field.getBackground();
        comboBox.addItem(null);
        BattleAction[] values = BattleAction.values();
        for (BattleAction value : values) {
            String text = bundle.getString(value.getKey());
            comboBox.addItem(text);
            reverseLookup.put(text, value);
        }
        defaultComboBoxBackground = comboBox.getBackground();
        String hiliteCsv = bundle.getString("battle.current.combatant.hilite");
        String[] hilites = StringUtils.split(hiliteCsv, ',');
        int red = Integer.parseInt(hilites[0]);
        int green = Integer.parseInt(hilites[1]);
        int blue = Integer.parseInt(hilites[2]);
        hiliteBackground = new Color(red, green, blue);
    }

    @Override
    public Object getCellEditorValue() {
        String text = (String) comboBox.getSelectedItem();
        if (text == null) {
            return null;
        } else {
            return reverseLookup.get(text);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value == null) {
            field.setText(null);
        } else {
            BattleAction battleAction = (BattleAction) value;
            String text = bundle.getString(battleAction.getKey());
            field.setText(text);
        }
        CombatantTableModel model = (CombatantTableModel) table.getModel();
        if (model.isHiLited(row, column)) {
            field.setBackground(hiliteBackground);
        } else {
            field.setBackground(defaultFieldBackground);
        }
        return field;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value == null) {
            comboBox.setSelectedItem(null);
        } else {
            BattleAction battleAction = (BattleAction) value;
            String text = bundle.getString(battleAction.getKey());
            comboBox.setSelectedItem(text);
        }
        CombatantTableModel model = (CombatantTableModel) table.getModel();
        if (model.isHiLited(row, column)) {
            field.setBackground(hiliteBackground);
        } else {
            field.setBackground(defaultComboBoxBackground);
        }
        return comboBox;
    }
}
