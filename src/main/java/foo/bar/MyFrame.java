package foo.bar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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

        String[] columnNames = {"First Name", "Last Name", ""};
        Object[][] data = {{"Homer", "Simpson", "delete Homer"}, {"Madge", "Simpson", "delete Madge"},
                {"Bart", "Simpson", "delete Bart"}, {"Lisa", "Simpson", "delete Lisa"},};

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                ((DefaultTableModel) table.getModel()).removeRow(modelRow);
            }
        };

        MyCellEditor buttonColumn = new MyCellEditor(table, delete, 0);
        //buttonColumn.setMnemonic(KeyEvent.VK_D);

        // Table scroll bars.
        table.setPreferredScrollableViewportSize(new Dimension(1000, 100));
        table.setFillsViewportHeight(true);
        desktop.add(new JScrollPane(table));

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Button clicked: actionEvent=\"" + e + "\".");
    }
}
