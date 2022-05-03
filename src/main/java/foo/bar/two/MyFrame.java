package foo.bar.two;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyFrame extends JFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyFrame.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting MyFrame");
        javax.swing.SwingUtilities.invokeLater(() -> {
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

        // Top button.
        JButton button = new JButton();
        button.setText("Add Character");
        button.setToolTipText("Adds a character to the current battle.");
        button.addActionListener(this);
        this.add(button, BorderLayout.NORTH);

        // Table.
        Object[] columnNames = {"Name", "ST", "DX", "IQ", "HT", "Speed"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        // Load data in table.
        Object[] row0 = {"Buddy", 13, 11, 9, 12, 5.75};
        model.addRow(row0);
        Object[] row1 = {"Friend", 11, 13, 9, 10, 5.75};
        model.addRow(row1);
        Object[] row2 = {"Guy", 7, 11, 13, 10, 5.25};
        model.addRow(row2);

        // Table selection listeners;
        table.getSelectionModel()
                .addListSelectionListener(e -> LOGGER.debug("Row selected: listSelectionEvent=\"" + e + "\"."));
        table.getColumnModel().getSelectionModel()
                .addListSelectionListener(e -> LOGGER.debug("Column selected: listSelectionEvent=\"" + e + "\"."));
        model.addTableModelListener(e -> LOGGER.debug("Table cell changed: tableModelEvent=\"" + e + "\"."));

        // Table scroll bars.
        table.setPreferredScrollableViewportSize(new Dimension(1000, 100));
        table.setFillsViewportHeight(true);
        desktop.add(new JScrollPane(table));

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        LOGGER.debug("Something happened: actionEvent=\"" + actionEvent + "\".");
    }
}
