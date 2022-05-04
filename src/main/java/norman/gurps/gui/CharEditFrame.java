package norman.gurps.gui;

import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

public class CharEditFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharEditFrame.class);
    private ResourceBundle bundle;
    private int gbcInsetx;
    private int gbcInsety;
    private Long modelId;
    private JTextField nameField;
    private JSpinner strengthSpinner;
    private JSpinner dexteritySpinner;
    private JSpinner intelligenceSpinner;
    private JSpinner healthSpinner;
    private JSpinner basicSpeedSpinner;
    private JButton saveButton;

    public CharEditFrame(GameChar gameChar, int frameCount) {
        super();
        initComponents(gameChar, frameCount);
    }

    private void initComponents(GameChar gameChar, int frameCount) {
        LOGGER.debug("Initializing character edit frame.");
        bundle = ResourceBundle.getBundle("message");
        setTitle(bundle.getString("char.frame.edit.title"));
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
        nameField = createField(nameCols, this, createGbc(1, 0));
        createLabel("char.strength", this, createGbc(0, 1));
        strengthSpinner = createSpinner(attrCols, this, createGbc(1, 1));
        createLabel("char.dexterity", this, createGbc(0, 2));
        dexteritySpinner = createSpinner(attrCols, this, createGbc(1, 2));
        createLabel("char.intelligence", this, createGbc(0, 3));
        intelligenceSpinner = createSpinner(attrCols, this, createGbc(1, 3));
        createLabel("char.health", this, createGbc(0, 4));
        healthSpinner = createSpinner(attrCols, this, createGbc(1, 4));
        createLabel("char.basic.speed", this, createGbc(0, 5));

        basicSpeedSpinner = createSpinner(speedCols, 0.00, 0.00, null, 0.25, this, createGbc(1, 5));
        //        SpinnerModel model = new SpinnerNumberModel(0.00, 0.00, null, 0.25);
        //        basicSpeedSpinner = new JSpinner(model);
        //        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) basicSpeedSpinner.getEditor();
        //        editor.getTextField().setColumns(speedCols);
        //        GridBagConstraints gbc = createGbc(1, 5);
        //        gbc.anchor = GridBagConstraints.LINE_START;
        //        this.add(basicSpeedSpinner, gbc);

        saveButton = createButton("char.save", this, createGbc(1, 6));

        pack();
        setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.frame.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.frame.offset.y"));
        setLocation(offsetx * frameCount, offsety * frameCount);

        setValues(gameChar);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(saveButton)) {
            saveChar();
        } else {
            LOGGER.warn("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    public GameChar toModel() {
        GameChar gameChar = new GameChar();
        gameChar.setId(modelId);
        gameChar.setName(StringUtils.trimToNull(nameField.getText()));
        gameChar.setStrength((Integer) strengthSpinner.getValue());
        gameChar.setDexterity((Integer) dexteritySpinner.getValue());
        gameChar.setIntelligence((Integer) intelligenceSpinner.getValue());
        gameChar.setHealth((Integer) healthSpinner.getValue());
        gameChar.setBasicSpeed((Double) basicSpeedSpinner.getValue());
        return gameChar;
    }

    private void setValues(GameChar gameChar) {
        modelId = gameChar.getId();
        nameField.setText(gameChar.getName());
        strengthSpinner.setValue(gameChar.getStrength());
        dexteritySpinner.setValue(gameChar.getDexterity());
        intelligenceSpinner.setValue(gameChar.getIntelligence());
        healthSpinner.setValue(gameChar.getHealth());
        basicSpeedSpinner.setValue(gameChar.getBasicSpeed());
    }

    private void saveChar() {
        GameChar gameChar = toModel();
        List<String> errors = GameCharService.validate(gameChar);

        GameCharService.save(gameChar);
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

    private JTextField createField(int columns, Container container, GridBagConstraints gbc) {
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

    private JSpinner createSpinner(int columns, Container container, GridBagConstraints gbc) {
        return createSpinner(columns, null, null, null, null, container, gbc);
    }

    private JSpinner createSpinner(int columns, Number val, Comparable min, Comparable max, Number step,
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
