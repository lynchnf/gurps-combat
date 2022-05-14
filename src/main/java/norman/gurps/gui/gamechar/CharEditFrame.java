package norman.gurps.gui.gamechar;

import norman.gurps.gui.ButtonColumn;
import norman.gurps.gui.ButtonDescriptor;
import norman.gurps.model.equipment.Shield;
import norman.gurps.model.gamechar.CharWeapon;
import norman.gurps.model.gamechar.GameChar;
import norman.gurps.service.GameCharService;
import norman.gurps.service.ShieldService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static norman.gurps.gui.GuiUtils.createButton;
import static norman.gurps.gui.GuiUtils.createComboBox;
import static norman.gurps.gui.GuiUtils.createField;
import static norman.gurps.gui.GuiUtils.createGbc;
import static norman.gurps.gui.GuiUtils.createLabel;
import static norman.gurps.gui.GuiUtils.createSpinner;
import static norman.gurps.gui.GuiUtils.makeScrollable;

public class CharEditFrame extends JInternalFrame implements ActionListener {
    private static Logger LOGGER = LoggerFactory.getLogger(CharEditFrame.class);
    private ResourceBundle bundle = ResourceBundle.getBundle("message");
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private GameChar gameChar;
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
    private JSpinner shieldSkillLevelSpinner;
    private JButton addWeaponButton;
    private ButtonColumn removeWeaponButtonColumn;
    private JTable weaponTable;
    private JSpinner weightCarriedSpinner;
    private JButton saveButton;

    public CharEditFrame(GameChar gameChar, int frameCount) {
        super();
        initComponents(gameChar, frameCount);
    }

    private void initComponents(GameChar gameChar, int frameCount) {
        LOGGER.debug("Initializing character edit frame.");
        setTitle(bundle.getString("char.frame.edit.title"));
        setLayout(new GridBagLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        int nameCols = Integer.parseInt(bundle.getString("char.name.width"));
        int integerCols = Integer.parseInt(bundle.getString("char.integer.width"));
        int doubleCols = Integer.parseInt(bundle.getString("char.double.width"));
        int weaponTableWidth = Integer.parseInt(bundle.getString("char.weapon.table.width"));
        int weaponTableHeight = Integer.parseInt(bundle.getString("char.weapon.table.height"));

        List<Shield> shields = ShieldService.findAll();
        List<String> shieldNames = new ArrayList<>();
        for (Shield shield : shields) {
            shieldNames.add(shield.getName());
        }

        this.gameChar = gameChar;
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
        shieldNameComboBox.setSelectedItem(gameChar.getShieldName());
        createLabel(null, "char.shield.level", null, this, createGbc(2, 8));
        shieldSkillLevelSpinner = createSpinner(integerCols, this, createGbc(3, 8));
        shieldSkillLevelSpinner.setValue(gameChar.getShieldSkillLevel());

        addWeaponButton = createButton("images/plus16.png", "char.weapon.add", "char.weapon.add.tool.tip", this, this,
                createGbc(0, 9));

        // Weapon table.
        CharWeaponTableModel weaponModel = new CharWeaponTableModel();
        List<CharWeapon> weapons = gameChar.getCharWeapons();
        for (CharWeapon weapon : weapons) {
            ButtonDescriptor descriptor =
                    new ButtonDescriptor("images/remove8.png", null, "char.weapon.remove.tool.tip");
            CharWeaponTableRow row = new CharWeaponTableRow(weapon, descriptor);
            weaponModel.addRow(row);
        }
        weaponTable = new JTable(weaponModel);

        // Renderer and editor for remove button.
        removeWeaponButtonColumn = new ButtonColumn(weaponTable, this);
        weaponTable.getColumnModel().getColumn(0).setCellRenderer(removeWeaponButtonColumn);
        weaponTable.getColumnModel().getColumn(0).setCellEditor(removeWeaponButtonColumn);

        // Renderer for weapon name.
        WeaponNameCellEditor weaponNameEditor = new WeaponNameCellEditor();
        TableColumn weaponNameColumn = weaponTable.getColumnModel().getColumn(1);
        weaponNameColumn.setCellEditor(weaponNameEditor);

        // Renderer for skill name.
        SkillNameCellEditor skillNameEditor = new SkillNameCellEditor();
        TableColumn skillNameColumn = weaponTable.getColumnModel().getColumn(2);
        skillNameColumn.setCellEditor(skillNameEditor);

        // Renderer and editor for skill level spinner.
        SkillLevelCellEditor skillLevelEditor = new SkillLevelCellEditor();
        TableColumn skillLevelColumn = weaponTable.getColumnModel().getColumn(3);
        skillLevelColumn.setCellEditor(skillLevelEditor);

        // Table column widths.
        String columnWidthCsv = bundle.getString("char.weapon.table.column.widths");
        String[] columnWidths = StringUtils.split(columnWidthCsv, ',');
        for (int columnIndex = 0; columnIndex < columnWidths.length; columnIndex++) {
            TableColumn column = weaponTable.getColumnModel().getColumn(columnIndex);
            int columnWidth = Integer.parseInt(columnWidths[columnIndex]);
            column.setPreferredWidth(columnWidth);
        }

        // Make table scrollable.
        JScrollPane scrollable = makeScrollable(weaponTable, weaponTableWidth, weaponTableHeight);
        GridBagConstraints gbc = createGbc(1, 9, 3);
        gbc.anchor = GridBagConstraints.LINE_START;
        add(scrollable, gbc);

        createLabel(null, "char.weight.carried", null, this, createGbc(0, 10));
        weightCarriedSpinner = createSpinner(doubleCols, 0.00, null, null, 0.01, this, createGbc(1, 10));
        weightCarriedSpinner.setValue(gameChar.getWeightCarried());

        saveButton = createButton(null, "char.save", null, this, this, createGbc(1, 11, 3));

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
            String selectedItem = (String) comboBox.getSelectedItem();
            gameChar.setShieldName(selectedItem);
        });
        shieldSkillLevelSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Integer value = (Integer) spinner.getValue();
            gameChar.setShieldSkillLevel(value);
        });
        weightCarriedSpinner.addChangeListener(e -> {
            JSpinner spinner = (JSpinner) e.getSource();
            Double value = (Double) spinner.getValue();
            gameChar.setWeightCarried(value);
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
        } else if (e.getSource().equals(addWeaponButton)) {
            addWeapon();
        } else if (e.getSource().equals(removeWeaponButtonColumn.getButton())) {
            removeWeapon();
        } else {
            LOGGER.warn("Unknown ActionEvent=\"" + ((AbstractButton) e.getSource()).getText() + "\"");
        }
    }

    private void saveChar() {
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
        gameChar.setShieldName((String) shieldNameComboBox.getSelectedItem());
        gameChar.setShieldSkillLevel((Integer) shieldSkillLevelSpinner.getValue());
        gameChar.setWeightCarried((Double) weightCarriedSpinner.getValue());

        CharWeaponTableModel model = (CharWeaponTableModel) weaponTable.getModel();
        List<CharWeaponTableRow> rows = model.getDataList();
        for (CharWeaponTableRow row : rows) {
            CharWeapon weapon = new CharWeapon();
            weapon.setWeaponName(StringUtils.trimToNull(row.getWeaponName()));
            weapon.setSkillName(StringUtils.trimToNull(row.getSkillName()));
            if (row.getSkillLevel() == null) {
                weapon.setSkillLevel(0);
            } else {
                weapon.setSkillLevel(row.getSkillLevel());
            }
            if (row.isFavorite() == null) {
                weapon.setFavorite(false);
            } else {
                weapon.setFavorite(row.isFavorite());
            }
            if (weapon.getWeaponName() != null || weapon.getSkillName() != null || weapon.getSkillLevel() != 0 ||
                    weapon.getFavorite()) {
                gameChar.getCharWeapons().add(weapon);
            }
        }

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

    private void addWeapon() {
        CharWeapon weapon = new CharWeapon();
        gameChar.getCharWeapons().add(weapon);
        ButtonDescriptor descriptor = new ButtonDescriptor("images/remove8.png", null, "char.weapon.remove.tool.tip");
        CharWeaponTableRow row = new CharWeaponTableRow(weapon, descriptor);
        CharWeaponTableModel model = (CharWeaponTableModel) weaponTable.getModel();
        model.addRow(row);
    }

    private void removeWeapon() {
        int rowIndex = removeWeaponButtonColumn.getEditingRow();
        CharWeaponTableModel model = (CharWeaponTableModel) weaponTable.getModel();
        model.removeRow(rowIndex);
    }
}
