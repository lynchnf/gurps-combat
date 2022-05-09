package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpinnerCellEditor.class);
    private final JSpinner spinner;

    public SpinnerCellEditor() {
        spinner = new JSpinner();
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value != null) {
            spinner.setValue(value);
        }
        return spinner;
    }
}
