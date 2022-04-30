package norman.gurps.gui;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class TableButtonRenderer implements TableCellRenderer {
    private TableCellRenderer defaultRenderer;

    public TableButtonRenderer(TableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value instanceof Component) {
            return (Component) value;
        }
        return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
