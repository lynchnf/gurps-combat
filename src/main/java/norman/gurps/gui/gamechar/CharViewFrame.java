package norman.gurps.gui.gamechar;

import norman.gurps.model.gamechar.CharWeapon;
import norman.gurps.model.gamechar.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import static norman.gurps.gui.GuiUtils.createButton;
import static norman.gurps.gui.GuiUtils.createFieldReadOnly;
import static norman.gurps.gui.GuiUtils.createGbc;
import static norman.gurps.gui.GuiUtils.createLabel;
import static norman.gurps.gui.GuiUtils.makeScrollable;

public class CharViewFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharViewFrame.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private Long modelId;
    private JTextField nameField;
    private JTextField strengthField;
    private JTextField dexterityField;
    private JTextField intelligenceField;
    private JTextField healthField;
    private JTextField hitPointsField;
    private JTextField basicSpeedField;
    private JTextField damageResistanceField;
    private JTextField shieldNameField;
    private JTextField shieldSkillLevelField;
    private JTextField weightCarriedField;
    private JList<CharWeapon> weaponList;
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

        int nameCols = Integer.parseInt(bundle.getString("char.name.width"));
        int integerCols = Integer.parseInt(bundle.getString("char.integer.width"));
        int doubleCols = Integer.parseInt(bundle.getString("char.double.width"));
        int stringCols = Integer.parseInt(bundle.getString("char.string.width"));
        int weaponTableWidth = Integer.parseInt(bundle.getString("char.weapon.table.width"));
        int weaponTableHeight = Integer.parseInt(bundle.getString("char.weapon.table.height"));

        modelId = gameChar.getId();
        createLabel(null, "char.name", null, this, createGbc(0, 0));
        nameField = createFieldReadOnly(nameCols, this, createGbc(1, 0, 3));
        nameField.setText(gameChar.getName());
        createLabel(null, "char.strength", null, this, createGbc(0, 1));
        strengthField = createFieldReadOnly(integerCols, this, createGbc(1, 1));
        strengthField.setText(String.valueOf(gameChar.getStrength()));
        createLabel(null, "char.dexterity", null, this, createGbc(0, 2));
        dexterityField = createFieldReadOnly(integerCols, this, createGbc(1, 2));
        dexterityField.setText(String.valueOf(gameChar.getDexterity()));
        createLabel(null, "char.intelligence", null, this, createGbc(0, 3));
        intelligenceField = createFieldReadOnly(integerCols, this, createGbc(1, 3));
        intelligenceField.setText(String.valueOf(gameChar.getIntelligence()));
        createLabel(null, "char.health", null, this, createGbc(0, 4));
        healthField = createFieldReadOnly(integerCols, this, createGbc(1, 4));
        healthField.setText(String.valueOf(gameChar.getHealth()));
        createLabel(null, "char.hit.points", null, this, createGbc(0, 5));
        hitPointsField = createFieldReadOnly(integerCols, this, createGbc(1, 5));
        hitPointsField.setText(String.valueOf(gameChar.getHitPoints()));
        createLabel(null, "char.basic.speed", null, this, createGbc(0, 6));
        basicSpeedField = createFieldReadOnly(doubleCols, this, createGbc(1, 6));
        basicSpeedField.setText(String.valueOf(gameChar.getBasicSpeed()));
        createLabel(null, "char.damage.resist", null, this, createGbc(0, 7));
        damageResistanceField = createFieldReadOnly(integerCols, this, createGbc(1, 7));
        damageResistanceField.setText(String.valueOf(gameChar.getDamageResistance()));
        createLabel(null, "char.shield.name", null, this, createGbc(0, 8));
        shieldNameField = createFieldReadOnly(stringCols, this, createGbc(1, 8));
        shieldNameField.setText(gameChar.getShieldName());
        createLabel(null, "char.shield.level", null, this, createGbc(2, 8));
        shieldSkillLevelField = createFieldReadOnly(integerCols, this, createGbc(3, 8));
        shieldSkillLevelField.setText(String.valueOf(gameChar.getShieldSkillLevel()));

        DefaultListModel<CharWeapon> weaponModel = new DefaultListModel<>();
        List<CharWeapon> weapons = gameChar.getCharWeapons();
        for (CharWeapon weapon : weapons) {
            weaponModel.addElement(weapon);
        }
        weaponList = new JList<>(weaponModel);
        JScrollPane scrollable = makeScrollable(weaponList, weaponTableWidth, weaponTableHeight);
        GridBagConstraints gbc = createGbc(1, 9, 3);
        gbc.anchor = GridBagConstraints.LINE_START;
        add(scrollable, gbc);

        createLabel(null, "char.weight.carried", null, this, createGbc(0, 10));
        weightCarriedField = createFieldReadOnly(doubleCols, this, createGbc(1, 10));
        weightCarriedField.setText(String.valueOf(gameChar.getWeightCarried()));

        deleteButton = createButton(null, "char.delete", null, this, this, createGbc(1, 11));

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
}
