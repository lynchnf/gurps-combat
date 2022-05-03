package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class CombatantButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatantButtonColumn.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private Object cellEditorValue;
    private final JButton rendererButton;
    private final JButton editorButton;

    public CombatantButtonColumn(String imagePath, String textKey, String toolTipKey, ActionListener listener,
            JTable table, int columnIndex) {
        bundle = ResourceBundle.getBundle("message");
        loader = Thread.currentThread().getContextClassLoader();

        rendererButton = new JButton();
        editorButton = new JButton();

        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            rendererButton.setIcon(icon);
            editorButton.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            rendererButton.setText(text);
            editorButton.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            rendererButton.setToolTipText(toolTip);
            editorButton.setToolTipText(toolTip);
        }
        if (listener != null) {
            editorButton.addActionListener(listener);
        }

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(columnIndex).setCellRenderer(this);
        columnModel.getColumn(columnIndex).setCellEditor(this);
    }

    @Override
    public Object getCellEditorValue() {
        return cellEditorValue;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        return rendererButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        cellEditorValue = value;
        return editorButton;
    }
}
