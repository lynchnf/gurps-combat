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

public class CharEditFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharEditFrame.class);
    private static int openFrameCount = 0;
    private ResourceBundle bundle;
    private Long modelId;
    private JTextField nameField;
    private JSpinner strengthSpinner;
    private JSpinner dexteritySpinner;
    private JSpinner intelligenceSpinner;
    private JSpinner healthSpinner;
    private JButton saveButton;

    public CharEditFrame(GameChar gameChar) {
        super();
        initComponents(gameChar);
    }

    private void initComponents(GameChar gameChar) {
        LOGGER.debug("Initializing character edit frame.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.CharEditFrame");
        setTitle(bundle.getString("char.edit.title") + " - " + (openFrameCount + 1));
        setLayout(new GridBagLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        int insetx = Integer.parseInt(bundle.getString("char.edit.insets.x"));
        int insety = Integer.parseInt(bundle.getString("char.edit.insets.y"));
        int nameCols = Integer.parseInt(bundle.getString("char.edit.name.columns"));
        int attrCols = Integer.parseInt(bundle.getString("char.edit.attribute.columns"));

        createLabel("char.edit.name", this, 0, 0, insetx, insety);
        nameField = createField(nameCols, this, 1, 0, insetx, insety);
        createLabel("char.edit.strength", this, 0, 1, insetx, insety);
        strengthSpinner = createSpinner(attrCols, this, 1, 1, insetx, insety);
        createLabel("char.edit.dexterity", this, 0, 2, insetx, insety);
        dexteritySpinner = createSpinner(attrCols, this, 1, 2, insetx, insety);
        createLabel("char.edit.intelligence", this, 0, 3, insetx, insety);
        intelligenceSpinner = createSpinner(attrCols, this, 1, 3, insetx, insety);
        createLabel("char.edit.health", this, 0, 4, insetx, insety);
        healthSpinner = createSpinner(attrCols, this, 1, 4, insetx, insety);
        saveButton = createButton("char.edit.save", this, 1, 5, insetx, insety);

        pack();
        setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.edit.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.edit.offset.y"));
        setLocation(offsetx * openFrameCount, offsety * openFrameCount);
        openFrameCount++;

        setValues(gameChar);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(saveButton)) {
            saveChar();
        } else {
            LOGGER.debug("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    public GameChar toModel() {
        GameChar gameChar = new GameChar();
        gameChar.setId(modelId);
        gameChar.setName(nameField.getText());
        gameChar.setStrength((Integer) strengthSpinner.getValue());
        gameChar.setDexterity((Integer) dexteritySpinner.getValue());
        gameChar.setIntelligence((Integer) intelligenceSpinner.getValue());
        gameChar.setHealth((Integer) healthSpinner.getValue());
        return gameChar;
    }

    private void setValues(GameChar gameChar) {
        modelId = gameChar.getId();
        nameField.setText(gameChar.getName());
        strengthSpinner.setValue(gameChar.getStrength());
        dexteritySpinner.setValue(gameChar.getDexterity());
        intelligenceSpinner.setValue(gameChar.getIntelligence());
        healthSpinner.setValue(gameChar.getHealth());
    }

    private void saveChar() {
        GameChar gameChar = toModel();
        GameCharService.save(gameChar);
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

    private JTextField createField(int columns, Container container, int gridx, int gridy, int insetx, int insety) {
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
}
