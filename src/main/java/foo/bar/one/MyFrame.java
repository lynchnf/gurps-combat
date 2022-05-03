package foo.bar.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;

public class MyFrame extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyFrame.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting MyFrame");
        SwingUtilities.invokeLater(() -> {
            MyFrame me = new MyFrame();
            me.doIt();
        });
    }

    private void doIt() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("My Frame");
        JDesktopPane desktop = new JDesktopPane();
        desktop.setLayout(new BorderLayout());
        desktop.setOpaque(false);
        setContentPane(desktop);

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // Combatant toolbar.
        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH);
        JButton button = new JButton();
        URL url = loader.getResource("norman/gurps/gui/character16.png");
        ImageIcon icon = new ImageIcon(url);
        button.setIcon(icon);
        button.setText("Add Character");
        button.setToolTipText("Adds a character to the current battle.");
        button.addActionListener(e -> LOGGER.debug("actionEvent=\"" + e + "\"."));
        toolBar.add(button);

        // Combatant table.
        MyTableModel model = new MyTableModel();
        JTable table = new JTable(model);
        MyButtonColumn buttonColumn = new MyButtonColumn(table, 0);

        JComboBox<MyAction> actionComboBox = new JComboBox<>(MyAction.values());
        DefaultCellEditor actionEditor = new DefaultCellEditor(actionComboBox);
        TableColumn actionColumn = table.getColumnModel().getColumn(0);
        actionColumn.setCellEditor(actionEditor);

        // Combatant table column widths.
        int[] columnWidths = {20, 530, 50, 50, 50, 50, 50}; // TODO Put these in a properties file somewhere.
        for (int columnIndex = 0; columnIndex < columnWidths.length; columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            column.setPreferredWidth(columnWidths[columnIndex]);
        }

        // Load data in combatant table.
        MyTableModelRow row0 = new MyTableModelRow("Buddy", 13, 11, 9, 12, 5.75);
        model.addRow(row0);
        MyTableModelRow row1 = new MyTableModelRow("Friend", 11, 13, 9, 10, 5.75);
        model.addRow(row1);
        MyTableModelRow row2 = new MyTableModelRow("Guy", 7, 11, 13, 10, 5.25);
        model.addRow(row2);

        // Make combatant table scrollable.
        table.setPreferredScrollableViewportSize(new Dimension(1000, 100));
        table.setFillsViewportHeight(true);
        desktop.add(new JScrollPane(table));

        pack();
        setVisible(true);
    }
}
