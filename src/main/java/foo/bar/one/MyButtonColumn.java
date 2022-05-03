package foo.bar.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.Component;

public class MyButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyButtonColumn.class);
    private Object cellEditorValue;
    private final JButton rendererButton;
    private final JButton editorButton;

    public MyButtonColumn(JTable table, int columnIndex) {
        rendererButton = new JButton();
        editorButton = new JButton();
        editorButton.addActionListener(e -> LOGGER.debug("actionEvent=\"" + e + "\"."));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(columnIndex).setCellRenderer(this);
        columnModel.getColumn(columnIndex).setCellEditor(this);
    }

    @Override // AbstractCellEditor
    public Object getCellEditorValue() {
        return cellEditorValue;
    }

    @Override // TableCellRenderer
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        return rendererButton;
    }

    @Override // TableCellEditor
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        cellEditorValue = value;
        return editorButton;
    }
}
