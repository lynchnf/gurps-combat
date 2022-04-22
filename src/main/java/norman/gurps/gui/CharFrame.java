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

public class CharFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharFrame.class);
    private static int openFrameCount = 0;
    private ResourceBundle bundle;
    private JTextField nameTextField;
    private JSpinner strengthSpinner;
    private JSpinner dexteritySpinner;
    private JSpinner intelligenceSpinner;
    private JSpinner healthSpinner;

    private JButton saveButton;

    public CharFrame() {
        super();
        initComponents(new GameChar());
    }

    public CharFrame(GameChar gameChar) {
        super();
        initComponents(gameChar);
    }

    private void initComponents(GameChar gameChar) {
        LOGGER.debug("Initializing character frame.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.CharFrame");
        setTitle(bundle.getString("char.title") + " - " + (openFrameCount + 1));
        setLayout(new GridBagLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        int insetx = Integer.parseInt(bundle.getString("char.insets.x"));
        int insety = Integer.parseInt(bundle.getString("char.insets.y"));
        int nameCols = Integer.parseInt(bundle.getString("char.name.columns"));
        int attrCols = Integer.parseInt(bundle.getString("char.attribute.columns"));

        createLabel("char.name", this, 0, 0, insetx, insety);
        nameTextField = createTextField(nameCols, this, 1, 0, insetx, insety);
        createLabel("char.strength", this, 0, 1, insetx, insety);
        strengthSpinner = createSpinner(attrCols, this, 1, 1, insetx, insety);
        createLabel("char.dexterity", this, 0, 2, insetx, insety);
        dexteritySpinner = createSpinner(attrCols, this, 1, 2, insetx, insety);
        createLabel("char.intelligence", this, 0, 3, insetx, insety);
        intelligenceSpinner = createSpinner(attrCols, this, 1, 3, insetx, insety);
        createLabel("char.health", this, 0, 4, insetx, insety);
        healthSpinner = createSpinner(attrCols, this, 1, 4, insetx, insety);
        saveButton = createButton("char.save", this, 1, 5, insetx, insety);

        this.pack();
        this.setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.offset.y"));
        setLocation(offsetx * openFrameCount, offsety * openFrameCount);
        openFrameCount++;

        nameTextField.setText(gameChar.getName());
        strengthSpinner.setValue(gameChar.getStrength());
        dexteritySpinner.setValue(gameChar.getDexterity());
        intelligenceSpinner.setValue(gameChar.getIntelligence());
        healthSpinner.setValue(gameChar.getHealth());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(saveButton)) {
            saveChar();
        } else {
            LOGGER.debug("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    private JLabel createLabel(String key, Container container, int gridx, int gridy, int insetx, int insety) {
        JLabel label = new JLabel(bundle.getString(key));
        GridBagConstraints constraints = createConstraints(gridx, gridy, insetx, insety);
        constraints.anchor = GridBagConstraints.LINE_END;
        container.add(label, constraints);
        return label;
    }

    private JTextField createTextField(int columns, Container container, int gridx, int gridy, int insetx, int insety) {
        JTextField field = new JTextField(columns);
        GridBagConstraints constraints = createConstraints(gridx, gridy, insetx, insety);
        constraints.anchor = GridBagConstraints.LINE_START;
        container.add(field, constraints);
        return field;
    }

    private JSpinner createSpinner(int columns, Container container, int gridx, int gridy, int insetx, int insety) {
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(0);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setColumns(columns);
        GridBagConstraints constraints = createConstraints(gridx, gridy, insetx, insety);
        constraints.anchor = GridBagConstraints.LINE_START;
        container.add(spinner, constraints);
        return spinner;
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

    private void saveChar() {
        GameChar gameChar = new GameChar();
        gameChar.setName(nameTextField.getText());
        gameChar.setStrength((Integer) strengthSpinner.getValue());
        gameChar.setDexterity((Integer) dexteritySpinner.getValue());
        gameChar.setIntelligence((Integer) intelligenceSpinner.getValue());
        gameChar.setHealth((Integer) healthSpinner.getValue());
        GameCharService.save(gameChar);
        doDefaultCloseAction();
    }
}
