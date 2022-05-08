package norman.gurps.gui;

import norman.gurps.model.GameChar;
import norman.gurps.model.Shield;
import norman.gurps.service.GameCharService;
import norman.gurps.service.ShieldService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CharEditFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharEditFrame.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private int gbcInsetx;
    private int gbcInsety;
    private Long modelId;
    private JTextField nameField;
    private JSpinner strengthSpinner;
    private JSpinner dexteritySpinner;
    private JSpinner intelligenceSpinner;
    private JSpinner healthSpinner;
    private JSpinner hitPointsSpinner;
    private JSpinner basicSpeedSpinner;
    private JSpinner damageResistanceSpinner;
    private JComboBox<String> shieldNameComboBox;
    private JTextField shieldDefenseBonusField;
    private JButton saveButton;

    public CharEditFrame(GameChar gameChar, int frameCount) {
        super();
        initComponents(gameChar, frameCount);
    }

    private void initComponents(GameChar gameChar, int frameCount) {
        LOGGER.debug("Initializing character edit frame.");
        bundle = ResourceBundle.getBundle("message");
        loader = Thread.currentThread().getContextClassLoader();
        setTitle(bundle.getString("char.frame.edit.title"));
        setLayout(new GridBagLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        gbcInsetx = Integer.parseInt(bundle.getString("char.frame.insets.x"));
        gbcInsety = Integer.parseInt(bundle.getString("char.frame.insets.y"));
        int nameCols = Integer.parseInt(bundle.getString("char.name.width"));
        int integerCols = Integer.parseInt(bundle.getString("char.integer.width"));
        int doubleCols = Integer.parseInt(bundle.getString("char.double.width"));
        List<Shield> shields = ShieldService.findAll();
        List<String> shieldNames = new ArrayList<>();
        for (Shield shield : shields) {
            shieldNames.add(shield.getName());
        }

        modelId = gameChar.getId();
        createLabel(null, "char.name", null, this, createGbc(0, 0));
        nameField = createField(nameCols, this, createGbc(1, 0, 3));
        nameField.setText(gameChar.getName());
        createLabel(null, "char.strength", null, this, createGbc(0, 1));
        strengthSpinner = createSpinner(integerCols, this, createGbc(1, 1));
        strengthSpinner.setValue(gameChar.getStrength());
        createLabel(null, "char.dexterity", null, this, createGbc(0, 2));
        dexteritySpinner = createSpinner(integerCols, this, createGbc(1, 2));
        dexteritySpinner.setValue(gameChar.getDexterity());
        createLabel(null, "char.intelligence", null, this, createGbc(0, 3));
        intelligenceSpinner = createSpinner(integerCols, this, createGbc(1, 3));
        intelligenceSpinner.setValue(gameChar.getIntelligence());
        createLabel(null, "char.health", null, this, createGbc(0, 4));
        healthSpinner = createSpinner(integerCols, this, createGbc(1, 4));
        healthSpinner.setValue(gameChar.getHealth());
        createLabel(null, "char.hit.points", null, this, createGbc(0, 5));
        hitPointsSpinner = createSpinner(integerCols, this, createGbc(1, 5));
        hitPointsSpinner.setValue(gameChar.getHitPoints());
        createLabel(null, "char.basic.speed", null, this, createGbc(0, 6));
        basicSpeedSpinner = createSpinner(doubleCols, 0.00, null, null, 0.25, this, createGbc(1, 6));
        basicSpeedSpinner.setValue(gameChar.getBasicSpeed());
        createLabel(null, "char.damage.resist", null, this, createGbc(0, 7));
        damageResistanceSpinner = createSpinner(integerCols, this, createGbc(1, 7));
        damageResistanceSpinner.setValue(gameChar.getDamageResistance());
        createLabel(null, "char.shield.name", null, this, createGbc(0, 8));
        shieldNameComboBox = createComboBox(shieldNames, this, createGbc(1, 8));
        int shieldIdx = shieldNames.indexOf(gameChar.getShieldName());
        if (shieldIdx >= 0) {
            shieldNameComboBox.setSelectedIndex(shieldIdx + 1);
        }
        createLabel(null, "char.shield.defense.bonus", null, this, createGbc(2, 8));
        shieldDefenseBonusField = createFieldReadOnly(integerCols, this, createGbc(3, 8));
        if (shieldIdx >= 0) {
            shieldDefenseBonusField.setText(String.valueOf(shields.get(shieldIdx).getDefenseBonus()));
        }
        saveButton = createButton(null, "char.save", null, this, this, createGbc(1, 9));

        strengthSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setStrength(value);
            hitPointsSpinner.setValue(gameChar.getHitPoints());
        });
        dexteritySpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setDexterity(value);
            basicSpeedSpinner.setValue(gameChar.getBasicSpeed());
        });
        intelligenceSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setIntelligence(value);
        });
        healthSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setHealth(value);
            basicSpeedSpinner.setValue(gameChar.getBasicSpeed());
        });
        hitPointsSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setHitPoints(value);
        });
        basicSpeedSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Double value = (Double) spinner.getValue();
            gameChar.setBasicSpeed(value);
        });
        damageResistanceSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setDamageResistance(value);
        });
        shieldNameComboBox.addActionListener(e -> {
            JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
            String name = (String) comboBox.getSelectedItem();
            gameChar.setShieldName(name);
            int idx = shieldNames.indexOf(name);
            if (idx < 0) {
                gameChar.setShieldDefenseBonus(null);
                shieldDefenseBonusField.setText(null);
            } else {
                Integer db = shields.get(idx).getDefenseBonus();
                gameChar.setShieldDefenseBonus(db);
                shieldDefenseBonusField.setText(String.valueOf(db));
            }
        });

        pack();
        setVisible(true);

        int offsetx = Integer.parseInt(bundle.getString("char.frame.offset.x"));
        int offsety = Integer.parseInt(bundle.getString("char.frame.offset.y"));
        setLocation(offsetx * frameCount, offsety * frameCount);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(saveButton)) {
            saveChar();
        } else {
            LOGGER.warn("Unknown ActionEvent=\"" + ((AbstractButton) e.getSource()).getText() + "\"");
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
        gameChar.setHitPoints((Integer) hitPointsSpinner.getValue());
        gameChar.setBasicSpeed((Double) basicSpeedSpinner.getValue());
        gameChar.setDamageResistance((Integer) damageResistanceSpinner.getValue());
        String name = (String) shieldNameComboBox.getSelectedItem();
        gameChar.setShieldName(name);
        String db = StringUtils.trimToNull(shieldDefenseBonusField.getText());
        if (db == null) {
            gameChar.setShieldDefenseBonus(null);
        } else {
            gameChar.setShieldDefenseBonus(Integer.valueOf(db));
        }
        return gameChar;
    }

    private void saveChar() {
        GameChar gameChar = toModel();
        List<String> errors = GameCharService.validate(gameChar);

        if (errors.isEmpty()) {
            GameCharService.save(gameChar);
            dispose();
        } else {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                if (i != 0) {
                    message.append(System.getProperty("line.separator"));
                }
                message.append(errors.get(i));
            }
            JOptionPane.showInternalMessageDialog(this, message.toString(), bundle.getString("char.error.title"),
                    JOptionPane.ERROR_MESSAGE, null);
        }
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

    private <T> JComboBox<T> createComboBox(List<T> items, Container container, GridBagConstraints gbc) {
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
        return createGbc(gridx, gridy, 1);
    }

    private GridBagConstraints createGbc(int gridx, int gridy, int gridwidth) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
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
}
