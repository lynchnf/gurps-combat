package foo.bar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;

public class JTableButtonTest extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;

    public JTableButtonTest() {
        setTitle("JTableButton Test");
        TableCellRenderer tableRenderer;
        table = new JTable(new JTableButtonModel());
        tableRenderer = table.getDefaultRenderer(JButton.class);
        table.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 300);
        setVisible(true);
    }

    public static void main(String[] args) {
        new JTableButtonTest();
    }
}
