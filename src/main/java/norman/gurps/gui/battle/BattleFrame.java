package norman.gurps.gui.battle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.gui.ButtonColumn;
import norman.gurps.gui.ButtonDescriptor;
import norman.gurps.model.battle.Battle;
import norman.gurps.model.battle.BattleLog;
import norman.gurps.model.battle.Combatant;
import norman.gurps.model.gamechar.GameChar;
import norman.gurps.service.BattleService;
import norman.gurps.service.GameCharService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static norman.gurps.gui.GuiUtils.createButton;
import static norman.gurps.gui.GuiUtils.createLabel;
import static norman.gurps.gui.GuiUtils.createList;
import static norman.gurps.gui.GuiUtils.createPanel;
import static norman.gurps.gui.GuiUtils.createToolBar;
import static norman.gurps.gui.GuiUtils.makeScrollable;

public class BattleFrame extends JInternalFrame implements ActionListener {
    private static Logger LOGGER = LoggerFactory.getLogger(BattleFrame.class);
    private ResourceBundle bundle = ResourceBundle.getBundle("message");
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private ObjectMapper mapper = new ObjectMapper();
    private Battle battle = new Battle();
    private List<BattleLog> battleLogs = new ArrayList<>();
    private JButton addCharButton;
    private JButton startButton;
    private JButton haltButton;
    private JTable combatantTable;
    private ButtonColumn combatantButtonColumn;
    private JList<BattleLog> logList;

    public BattleFrame() {
        super();
        initComponents();
    }

    private void initComponents() {
        LOGGER.debug("Initializing battle frame.");
        setTitle(bundle.getString("battle.frame.title"));
        setLayout(new BorderLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);

        // Battlefield map.
        JLabel mapLabel = createLabel("images/battlefield.png", null, null, null);
        JScrollPane mapScroll = makeScrollable(mapLabel, 100, 100);

        // Combatants pane with toolbar.
        JPanel combatantPanel = createPanel(null);
        JToolBar toolBar = createToolBar(combatantPanel);
        addCharButton = createButton("images/character16.png", null, "battle.add.char.tool.tip", this, toolBar);
        startButton = createButton("images/start16.png", null, "battle.start.tool.tip", this, toolBar);
        haltButton = createButton("images/halt16.png", null, "battle.halt.tool.tip", this, toolBar);
        haltButton.setEnabled(false);

        // Combatant table.
        CombatantTableModel model = new CombatantTableModel(battle);
        combatantTable = new JTable(model);

        // Renderer and editor for remove button.
        combatantButtonColumn = new ButtonColumn(combatantTable, this);
        combatantTable.getColumnModel().getColumn(0).setCellRenderer(combatantButtonColumn);
        combatantTable.getColumnModel().getColumn(0).setCellEditor(combatantButtonColumn);

        // Renderer for shield.
        CombatantWeaponColumn weaponColumn = new CombatantWeaponColumn();
        combatantTable.getColumnModel().getColumn(11).setCellRenderer(weaponColumn);
        combatantTable.getColumnModel().getColumn(11).setCellEditor(weaponColumn);

        // Renderer for shield.
        CombatantShieldColumn shieldColumn = new CombatantShieldColumn();
        combatantTable.getColumnModel().getColumn(12).setCellRenderer(shieldColumn);
        combatantTable.getColumnModel().getColumn(12).setCellEditor(shieldColumn);

        // Renderer for action.
        BattleActionColumn actionColumn = new BattleActionColumn();
        combatantTable.getColumnModel().getColumn(13).setCellRenderer(actionColumn);
        combatantTable.getColumnModel().getColumn(13).setCellEditor(actionColumn);

        // Combatant table column widths.
        String columnWidthCsv = bundle.getString("combatant.table.column.widths");
        String[] columnWidths = StringUtils.split(columnWidthCsv, ',');
        for (int columnIndex = 0; columnIndex < columnWidths.length; columnIndex++) {
            TableColumn column = combatantTable.getColumnModel().getColumn(columnIndex);
            int columnWidth = Integer.parseInt(columnWidths[columnIndex]);
            column.setPreferredWidth(columnWidth);
        }

        // Make combatant table scrollable.
        JScrollPane combatantScrollable = makeScrollable(combatantTable, 100, 200);
        combatantPanel.add(combatantScrollable);

        // Battle logs.
        BattleLog log = new BattleLog(bundle.getString("battle.log.created"), null);
        battleLogs.add(log);
        logList = createList(battleLogs, null);
        JScrollPane logScroll = makeScrollable(logList, 100, 100);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, combatantPanel, logScroll);
        JSplitPane outerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapScroll, innerSplitPane);
        this.add(outerSplitPane);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addCharButton)) {
            addChar();
        } else if (e.getSource().equals(startButton)) {
            start();
        } else if (e.getSource().equals(combatantButtonColumn.getButton())) {
            removeChar();
        } else {
            LOGGER.debug("ActionEvent=\"" + e + "\"");
        }
    }

    private void addChar() {
        // Select character to add.
        GameChar gameChar = showSelectCharDialog("images/character32.png", "battle.add.char.dialog.title",
                "battle.add.char.dialog.message", this);
        if (gameChar != null) {

            // Get names for all combatants currently in the battle so the new combatant will have a different name.
            CombatantTableModel model = (CombatantTableModel) combatantTable.getModel();
            List<String> existingNames = new ArrayList<>();
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = (String) model.getValueAt(row, 1);
                existingNames.add(name);
            }
            Combatant combatant = new Combatant(gameChar, existingNames);

            // Add character to battle and table.
            battle.getCombatants().add(combatant);
            ButtonDescriptor descriptor =
                    new ButtonDescriptor("images/remove8.png", null, "combatant.table.remove.char.tool.tip");
            CombatantTableRow row = new CombatantTableRow(combatant, descriptor);
            model.addRow(row);

            // Add log saying we added character.
            String message = String.format(bundle.getString("battle.log.char.added"), combatant.getName());
            addBattleLog(message);
        }
    }

    private void removeChar() {
        // Remove character from battle and table.
        int rowIndex = combatantButtonColumn.getEditingRow();
        CombatantTableModel model = (CombatantTableModel) combatantTable.getModel();
        String name = (String) model.getValueAt(rowIndex, 1);
        battle.getCombatants().remove(rowIndex);
        model.removeRow(rowIndex);

        // Add log saying we removed character.
        String message = String.format(bundle.getString("battle.log.char.removed"), name);
        addBattleLog(message);
    }

    private void start() {
        // Before we start the battle, validate it.
        List<String> errors = BattleService.validate(battle);

        // If validation errors, display them.
        if (!errors.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                if (i != 0) {
                    message.append(System.getProperty("line.separator"));
                }
                message.append(errors.get(i));
            }
            JOptionPane.showInternalMessageDialog(this, message.toString(), bundle.getString("battle.error.title"),
                    JOptionPane.ERROR_MESSAGE, null);
        } else {
            // If no errors, start battle.
            BattleService.start(battle);
            CombatantTableModel model = (CombatantTableModel) combatantTable.getModel();
            model.sortDataAndDisableRemove();

            addCharButton.setEnabled(false);
            startButton.setEnabled(false);
            haltButton.setEnabled(true);

            // Add log saying we started battle.
            String message = bundle.getString("battle.log.start");
            addBattleLog(message);
        }
    }

    private void addBattleLog(String message) {
        try {
            // Save current state of battle.
            String battleJson = mapper.writeValueAsString(battle);

            BattleLog log = new BattleLog(message, battleJson);
            battleLogs.add(log);
            DefaultListModel<BattleLog> logListModel = (DefaultListModel<BattleLog>) logList.getModel();
            logListModel.addElement(log);
        } catch (JsonProcessingException e) {
            throw new LoggingException(LOGGER, "Unable to convert to JSON: battle=\"" + battle + "\".");
        }
    }

    // COMMON METHODS // TODO Refactor these someday.

    private GameChar showSelectCharDialog(String imagePath, String titleKey, String messageKey,
            Component parentComponent) {
        Icon icon = null;
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            icon = new ImageIcon(url);
        }
        String title = null;
        if (titleKey != null) {
            title = bundle.getString(titleKey);
        }
        Object message = null;
        if (messageKey != null) {
            message = bundle.getString(messageKey);
        }
        List<GameChar> allGameChars = GameCharService.findAll();
        allGameChars.sort(Comparator.comparing(GameChar::getName));
        GameChar[] selectionValues = allGameChars.toArray(new GameChar[0]);
        return (GameChar) JOptionPane.showInternalInputDialog(parentComponent, message, title,
                JOptionPane.PLAIN_MESSAGE, icon, selectionValues, null);
    }
}
