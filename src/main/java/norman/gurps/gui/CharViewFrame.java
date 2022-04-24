package norman.gurps.gui;

import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class CharViewFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharViewFrame.class);
    private static int openFrameCount = 0;
    private ResourceBundle bundle;
    private Long modelId;
    private JTextField nameField;
    private JTextField strengthField;
    private JTextField dexterityField;
    private JTextField intelligenceField;
    private JTextField healthField;
    private JButton deleteButton;

    public CharViewFrame(GameChar gameChar) {
        super();
        initComponents(gameChar);
    }

    private void initComponents(GameChar gameChar) {
        LOGGER.debug("Initializing character view frame.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.CharViewFrame");
        setTitle(bundle.getString("char.view.title") + " - " + (openFrameCount + 1));
        setLayout(new GridBagLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        int insetx = Integer.parseInt(bundle.getString("char.view.insets.x"));
        int insety = Integer.parseInt(bundle.getString("char.view.insets.y"));
        int nameCols = Integer.parseInt(bundle.getString("char.view.name.columns"));
        int attrCols = Integer.parseInt(bundle.getString("char.view.attribute.columns"));

        createLabel("char.view.name", this, 0, 0, insetx, insety);
        nameField = createReadOnly(nameCols, this, 1, 0, insetx, insety);
        createLabel("char.view.strength", this, 0, 1, insetx, insety);
        strengthField = createReadOnly(attrCols, this, 1, 1, insetx, insety);
        createLabel("char.view.dexterity", this, 0, 2, insetx, insety);
        dexterityField = createReadOnly(attrCols, this, 1, 2, insetx, insety);
        createLabel("char.view.intelligence", this, 0, 3, insetx, insety);
        intelligenceField = createReadOnly(attrCols, this, 1, 3, insetx, insety);
        createLabel("char.view.health", this, 0, 4, insetx, insety);
        healthField = createReadOnly(attrCols, this, 1, 4, insetx, insety);
        deleteButton = createButton("char.view.delete", this, 1, 5, insetx, insety);

        this.pack();
        this.setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.view.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.view.offset.y"));
        setLocation(offsetx * openFrameCount, offsety * openFrameCount);
        openFrameCount++;

        setValues(gameChar);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(deleteButton)) {
            deleteChar();
        } else {
            LOGGER.debug("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    private void setValues(GameChar gameChar) {
        modelId = gameChar.getId();
        nameField.setText(gameChar.getName());
        strengthField.setText(String.valueOf(gameChar.getStrength()));
        dexterityField.setText(String.valueOf(gameChar.getDexterity()));
        intelligenceField.setText(String.valueOf(gameChar.getIntelligence()));
        healthField.setText(String.valueOf(gameChar.getHealth()));
    }

    private void deleteChar() {
        GameCharService.delete(modelId);
        doDefaultCloseAction();
    }

    // COMMON METHODS // TODO Refactor these someday.

    private JLabel createLabel(String key, Container container, int gridx, int gridy, int insetx, int insety) {
        JLabel label = new JLabel(bundle.getString(key));
        GridBagConstraints constraints = createConstraints(gridx, gridy, insetx, insety);
        constraints.anchor = GridBagConstraints.LINE_END;
        container.add(label, constraints);
        return label;
    }

    private JTextField createReadOnly(int columns, Container container, int gridx, int gridy, int insetx, int insety) {
        JTextField field = new JTextField(columns);
        field.setEditable(false);
        GridBagConstraints constraints = createConstraints(gridx, gridy, insetx, insety);
        constraints.anchor = GridBagConstraints.LINE_START;
        container.add(field, constraints);
        return field;
    }

    private JButton createButton(String key, Container container, int gridx, int gridy, int insetx, int insety) {
        JButton button = new JButton(bundle.getString(key));
        button.addActionListener(this);
        GridBagConstraints constraints = createConstraints(gridx, gridy, insetx, insety);
        constraints.anchor = GridBagConstraints.LINE_START;
        container.add(button, constraints);
        return button;
    }

    private GridBagConstraints createConstraints(int gridx, int gridy, int insetx, int insety) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.insets = new Insets(insety, insetx, insety, insetx);
        return constraints;
    }
}
