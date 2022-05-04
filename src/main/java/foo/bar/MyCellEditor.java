package foo.bar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCellEditor.class);
    private JTable table;
    private Action action;
    private int columnIndex;
    private Object cellEditorValue;
    private MyButton buttonRenderer;
    private MyButton buttonEditor;

    public MyCellEditor(JTable table, Action action, int columnIndex) {
        LOGGER.debug("MyCellEditor constructed");
        this.table = table;
        this.action = action;
        this.columnIndex = columnIndex;

        buttonRenderer = new MyButton();
        buttonEditor = new MyButton();
        buttonEditor.addActionListener(this);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(columnIndex).setCellRenderer(this);
        columnModel.getColumn(columnIndex).setCellEditor(this);
    }

    @Override // AbstractCellEditor
    public Object getCellEditorValue() {
        LOGGER.debug("MyCellEditor.getCellEditorValue()");
        return cellEditorValue;
    }

    @Override // TableCellRenderer
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        LOGGER.debug("MyCellEditor.getTableCellRendererComponent(" + value + ", " + row + ", " + column + ")");
        buttonRenderer.setText(value.toString());
        return buttonRenderer;
    }

    @Override // TableCellEditor
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        LOGGER.debug("MyCellEditor.getTableCellEditorComponent(" + value + ", " + row + ", " + column + ")");
        this.cellEditorValue = value;
        buttonEditor.setText(value.toString());
        return buttonEditor;
    }

    @Override // ActionListener
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("MyCellEditor.actionPerformed(" + e + ")");
        int row = table.convertRowIndexToModel(table.getEditingRow());
        fireEditingStopped();
        ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, String.valueOf(row));
        action.actionPerformed(event);
    }
}
