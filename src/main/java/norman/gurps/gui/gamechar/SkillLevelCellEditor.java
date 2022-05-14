package norman.gurps.gui.gamechar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class SkillLevelCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(SkillLevelCellEditor.class);
    private JSpinner spinner = new JSpinner();

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value == null) {
            spinner.setValue(0);
        } else {
            spinner.setValue(value);
        }
        return spinner;
    }
}
