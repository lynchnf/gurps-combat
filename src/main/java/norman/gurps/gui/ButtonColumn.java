package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.event.ActionListener;

public class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private static Logger LOGGER = LoggerFactory.getLogger(ButtonColumn.class);
    private JTable table;
    private JButton button;
    private Object value;

    public ButtonColumn(JTable table, ActionListener listener) {
        this.table = table;
        button = new JButton();
        button.addActionListener(listener);
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        return describeButton((ButtonDescriptor) value);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;
        return describeButton((ButtonDescriptor) value);
    }

    public JButton getButton() {
        return button;
    }

    public int getEditingRow() {
        int row = table.convertRowIndexToModel(table.getEditingRow());
        fireEditingStopped();
        return row;
    }

    private JButton describeButton(ButtonDescriptor value) {
        ButtonDescriptor descriptor = value;
        if (descriptor.getIcon() != null) {
            button.setIcon(descriptor.getIcon());
        }
        if (descriptor.getText() != null) {
            button.setText(descriptor.getText());
        }
        if (descriptor.getToolTip() != null) {
            button.setToolTipText(descriptor.getToolTip());
        }
        button.setEnabled(descriptor.isEnabled());
        return button;
    }
}
