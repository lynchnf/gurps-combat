package norman.gurps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiUtils.class);
    private static final ResourceBundle bundle = ResourceBundle.getBundle("message");
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static final int gbcInsetx = Integer.parseInt(bundle.getString("grid.bag.constraints.insets.x"));
    private static final int gbcInsety = Integer.parseInt(bundle.getString("grid.bag.constraints.insets.y"));

    public static JButton createButton(String imagePath, String textKey, String toolTipKey, ActionListener listener,
            Container container) {
        return createButton(imagePath, textKey, toolTipKey, listener, container, null);
    }

    public static JButton createButton(String imagePath, String textKey, String toolTipKey, ActionListener listener,
            Container container, GridBagConstraints gbc) {
        JButton button = new JButton();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            button.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            button.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            button.setToolTipText(toolTip);
        }
        if (listener != null) {
            button.addActionListener(listener);
        }
        if (container != null) {
            if (gbc != null) {
                gbc.anchor = GridBagConstraints.LINE_START;
                container.add(button, gbc);
            } else {
                container.add(button);
            }
        }
        return button;
    }

    public static <T> JComboBox<T> createComboBox(List<T> items, Container container) {
        return createComboBox(items, container, null);
    }

    public static <T> JComboBox<T> createComboBox(List<T> items, Container container, GridBagConstraints gbc) {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.addItem(null);
        for (T item : items) {
            comboBox.addItem(item);
        }
        if (container != null) {
            if (gbc != null) {
                gbc.anchor = GridBagConstraints.LINE_START;
                container.add(comboBox, gbc);
            } else {
                container.add(comboBox);
            }
        }
        return comboBox;
    }

    public static JTextField createField(int columns, Container container, GridBagConstraints gbc) {
        JTextField field = new JTextField(columns);
        if (container != null) {
            if (gbc != null) {
                gbc.anchor = GridBagConstraints.LINE_START;
                container.add(field, gbc);
            } else {
                container.add(field);
            }
        }
        return field;
    }

    public static JTextField createFieldReadOnly(int columns, Container container, GridBagConstraints gbc) {
        JTextField field = new JTextField(columns);
        field.setEditable(false);
        if (container != null) {
            if (gbc != null) {
                gbc.anchor = GridBagConstraints.LINE_START;
                container.add(field, gbc);
            } else {
                container.add(field);
            }
        }
        return field;
    }

    public static GridBagConstraints createGbc(int gridx, int gridy) {
        return createGbc(gridx, gridy, 1, 1);
    }

    public static GridBagConstraints createGbc(int gridx, int gridy, int gridwidth) {
        return createGbc(gridx, gridy, gridwidth, 1);
    }

    public static GridBagConstraints createGbc(int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.insets = new Insets(gbcInsety, gbcInsetx, gbcInsety, gbcInsetx);
        return constraints;
    }

    public static JLabel createLabel(String imagePath, String textKey, String toolTipKey, Container container) {
        return createLabel(imagePath, textKey, toolTipKey, container, null);
    }

    public static JLabel createLabel(String imagePath, String textKey, String toolTipKey, Container container,
            GridBagConstraints gbc) {
        JLabel label = new JLabel();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            label.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            label.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            label.setToolTipText(toolTip);
        }
        if (container != null) {
            if (gbc != null) {
                gbc.anchor = GridBagConstraints.LINE_END;
                container.add(label, gbc);
            } else {
                container.add(label);
            }
        }
        return label;
    }

    public static <T> JList<T> createList(List<T> elements, Container container) {
        DefaultListModel<T> model = new DefaultListModel<>();
        JList<T> list = new JList<>(model);
        if (elements != null) {
            for (T element : elements) {
                model.addElement(element);
            }
        }
        if (container != null) {
            container.add(list);
        }
        return list;
    }

    public static JMenu createMenu(String imagePath, String textKey, String toolTipKey, JMenuBar bar) {
        JMenu menu = new JMenu();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            menu.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            menu.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            menu.setToolTipText(toolTip);
        }
        if (bar != null) {
            bar.add(menu);
        }
        return menu;
    }

    public static JMenuItem createMenuItem(String imagePath, String textKey, String toolTipKey, ActionListener listener,
            JMenu menu) {
        JMenuItem item = new JMenuItem();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon icon = new ImageIcon(url);
            item.setIcon(icon);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            item.setText(text);
        }
        if (toolTipKey != null) {
            String toolTip = bundle.getString(toolTipKey);
            item.setToolTipText(toolTip);
        }
        if (listener != null) {
            item.addActionListener(listener);
        }
        if (menu != null) {
            menu.add(item);
        }
        return item;
    }

    public static JPanel createPanel(Container container) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        if (container != null) {
            container.add(panel);
        }
        return panel;
    }

    public static JSpinner createSpinner(int columns, Container container) {
        return createSpinner(columns, null, null, null, null, container, null);
    }

    public static JSpinner createSpinner(int columns, Container container, GridBagConstraints gbc) {
        return createSpinner(columns, null, null, null, null, container, gbc);
    }

    public static JSpinner createSpinner(int columns, Number val, Comparable min, Comparable max, Number step,
            Container container) {
        return createSpinner(columns, val, min, max, step, container, null);
    }

    public static JSpinner createSpinner(int columns, Number val, Comparable min, Comparable max, Number step,
            Container container, GridBagConstraints gbc) {
        Number value = Integer.valueOf(0);
        if (val != null) {
            value = val;
        }
        Number stepSize = Integer.valueOf(1);
        if (step != null) {
            stepSize = step;
        }
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, stepSize);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setColumns(columns);
        if (container != null) {
            if (gbc != null) {
                gbc.anchor = GridBagConstraints.LINE_START;
                container.add(spinner, gbc);
            } else {
                container.add(spinner);
            }
        }
        return spinner;
    }

    public static JToolBar createToolBar(Container container) {
        JToolBar bar = new JToolBar();
        if (container != null) {
            container.add(bar, BorderLayout.NORTH);
        }
        return bar;
    }

    public static JScrollPane makeScrollable(Component view, int width, int height) {
        JScrollPane scrollable = new JScrollPane(view);
        scrollable.setPreferredSize(new Dimension(width, height));
        return scrollable;
    }
}
