package norman.gurps.gui;

import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class CharViewFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharViewFrame.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private int gbcInsetx;
    private int gbcInsety;
    private Long modelId;
    private JTextField nameField;
    private JTextField strengthField;
    private JTextField dexterityField;
    private JTextField intelligenceField;
    private JTextField healthField;
    private JTextField hitPointsField;
    private JTextField basicSpeedField;
    private JTextField damageResistanceField;
    private JButton deleteButton;

    public CharViewFrame(GameChar gameChar, int frameCount) {
        super();
        initComponents(gameChar, frameCount);
    }

    private void initComponents(GameChar gameChar, int frameCount) {
        LOGGER.debug("Initializing character view frame.");
        bundle = ResourceBundle.getBundle("message");
        loader = Thread.currentThread().getContextClassLoader();
        setTitle(bundle.getString("char.frame.view.title"));
        setLayout(new GridBagLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        gbcInsetx = Integer.parseInt(bundle.getString("char.frame.insets.x"));
        gbcInsety = Integer.parseInt(bundle.getString("char.frame.insets.y"));
        int nameCols = Integer.parseInt(bundle.getString("char.name.width"));
        int attrCols = Integer.parseInt(bundle.getString("char.attribute.width"));
        int speedCols = Integer.parseInt(bundle.getString("char.basic.speed.width"));

        modelId = gameChar.getId();
        createLabel(null, "char.name", null, this, createGbc(0, 0));
        nameField = createFieldReadOnly(nameCols, this, createGbc(1, 0));
        nameField.setText(gameChar.getName());
        createLabel(null, "char.strength", null, this, createGbc(0, 1));
        strengthField = createFieldReadOnly(attrCols, this, createGbc(1, 1));
        strengthField.setText(String.valueOf(gameChar.getStrength()));
        createLabel(null, "char.dexterity", null, this, createGbc(0, 2));
        dexterityField = createFieldReadOnly(attrCols, this, createGbc(1, 2));
        dexterityField.setText(String.valueOf(gameChar.getDexterity()));
        createLabel(null, "char.intelligence", null, this, createGbc(0, 3));
        intelligenceField = createFieldReadOnly(attrCols, this, createGbc(1, 3));
        intelligenceField.setText(String.valueOf(gameChar.getIntelligence()));
        createLabel(null, "char.health", null, this, createGbc(0, 4));
        healthField = createFieldReadOnly(attrCols, this, createGbc(1, 4));
        healthField.setText(String.valueOf(gameChar.getHealth()));
        createLabel(null, "char.hit.points", null, this, createGbc(0, 5));
        hitPointsField = createFieldReadOnly(attrCols, this, createGbc(1, 5));
        hitPointsField.setText(String.valueOf(gameChar.getHitPoints()));
        createLabel(null, "char.basic.speed", null, this, createGbc(0, 6));
        basicSpeedField = createFieldReadOnly(speedCols, this, createGbc(1, 6));
        basicSpeedField.setText(String.valueOf(gameChar.getBasicSpeed()));
        createLabel(null, "char.damage.resist", null, this, createGbc(0, 7));
        damageResistanceField = createFieldReadOnly(attrCols, this, createGbc(1, 7));
        damageResistanceField.setText(String.valueOf(gameChar.getDamageResistance()));
        deleteButton = createButton(null, "char.delete", null, this, this, createGbc(1, 8));

        pack();
        setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.frame.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.frame.offset.y"));
        setLocation(offsetx * frameCount, offsety * frameCount);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(deleteButton)) {
            deleteChar();
        } else {
            LOGGER.warn("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    private void deleteChar() {
        GameCharService.delete(modelId);
        dispose();
    }

    // COMMON METHODS // TODO Refactor these someday.

    private JButton createButton(String imagePath, String textKey, String toolTipKey, ActionListener listener,
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

    private JTextField createFieldReadOnly(int columns, Container container, GridBagConstraints gbc) {
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

    private GridBagConstraints createGbc(int gridx, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.insets = new Insets(gbcInsety, gbcInsetx, gbcInsety, gbcInsetx);
        return constraints;
    }

    private JLabel createLabel(String imagePath, String textKey, String toolTipKey, Container container,
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
}
