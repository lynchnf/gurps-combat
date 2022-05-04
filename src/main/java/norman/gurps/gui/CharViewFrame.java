package norman.gurps.gui;

import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
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
import java.util.ResourceBundle;

public class CharViewFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharViewFrame.class);
    private ResourceBundle bundle;
    private int gbcInsetx;
    private int gbcInsety;
    private Long modelId;
    private JTextField nameField;
    private JTextField strengthField;
    private JTextField dexterityField;
    private JTextField intelligenceField;
    private JTextField healthField;
    private JTextField basicSpeedField;
    private JButton deleteButton;

    public CharViewFrame(GameChar gameChar, int frameCount) {
        super();
        initComponents(gameChar, frameCount);
    }

    private void initComponents(GameChar gameChar, int frameCount) {
        LOGGER.debug("Initializing character view frame.");
        bundle = ResourceBundle.getBundle("message");
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

        createLabel("char.name", this, createGbc(0, 0));
        nameField = createReadOnlyField(nameCols, this, createGbc(1, 0));
        createLabel("char.strength", this, createGbc(0, 1));
        strengthField = createReadOnlyField(attrCols, this, createGbc(1, 1));
        createLabel("char.dexterity", this, createGbc(0, 2));
        dexterityField = createReadOnlyField(attrCols, this, createGbc(1, 2));
        createLabel("char.intelligence", this, createGbc(0, 3));
        intelligenceField = createReadOnlyField(attrCols, this, createGbc(1, 3));
        createLabel("char.health", this, createGbc(0, 4));
        healthField = createReadOnlyField(attrCols, this, createGbc(1, 4));
        createLabel("char.basic.speed", this, createGbc(0, 5));
        basicSpeedField = createReadOnlyField(speedCols, this, createGbc(1, 5));
        deleteButton = createButton("char.delete", this, createGbc(1, 6));

        pack();
        setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.frame.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.frame.offset.y"));
        setLocation(offsetx * frameCount, offsety * frameCount);

        setValues(gameChar);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(deleteButton)) {
            deleteChar();
        } else {
            LOGGER.warn("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    private void setValues(GameChar gameChar) {
        modelId = gameChar.getId();
        nameField.setText(gameChar.getName());
        strengthField.setText(String.valueOf(gameChar.getStrength()));
        dexterityField.setText(String.valueOf(gameChar.getDexterity()));
        intelligenceField.setText(String.valueOf(gameChar.getIntelligence()));
        healthField.setText(String.valueOf(gameChar.getHealth()));
        basicSpeedField.setText(String.valueOf(gameChar.getBasicSpeed()));
    }

    private void deleteChar() {
        GameCharService.delete(modelId);
        dispose();
    }

    // COMMON METHODS // TODO Refactor these someday.

    private JLabel createLabel(String key, Container container, GridBagConstraints gbc) {
        JLabel label = new JLabel(bundle.getString(key));
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

    private JTextField createReadOnlyField(int columns, Container container, GridBagConstraints gbc) {
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

    private JButton createButton(String key, Container container, GridBagConstraints gbc) {
        JButton button = new JButton(bundle.getString(key));
        button.addActionListener(this);
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

    private GridBagConstraints createGbc(int gridx, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.insets = new Insets(gbcInsety, gbcInsetx, gbcInsety, gbcInsetx);
        return constraints;
    }
}
