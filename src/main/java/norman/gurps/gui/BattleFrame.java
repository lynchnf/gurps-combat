package norman.gurps.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.model.Battle;
import norman.gurps.model.BattleAction;
import norman.gurps.model.BattleLog;
import norman.gurps.model.Combatant;
import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class BattleFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleFrame.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private ObjectMapper mapper;
    private Battle battle;
    private JButton addCharButton;
    private JButton addGroupButton;
    private JButton startButton;
    private JTable combatantTable;
    private ButtonColumn combatantButtonColumn;

    private JList<BattleLog> logList;

    public BattleFrame() {
        super();
        initComponents();
    }

    private void initComponents() {
        LOGGER.debug("Initializing battle frame.");
        bundle = ResourceBundle.getBundle("message");
        loader = Thread.currentThread().getContextClassLoader();
        mapper = new ObjectMapper();
        setTitle(bundle.getString("battle.frame.title"));
        setLayout(new BorderLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        battle = new Battle();
        // Battlefield map.
        JLabel mapLabel = createLabel("images/battlefield.png", null, null);
        JScrollPane mapScroll = makeScrollable(mapLabel, 100, 100);

        // Combatants pane with toolbar.
        JPanel combatantPanel = createPanel(null);
        JToolBar toolBar = createToolBar(combatantPanel);
        addCharButton = createButton("images/character16.png", null, "battle.add.char.tool.tip", this, toolBar);
        addGroupButton = createButton("images/group16.png", null, "battle.add.group.tool.tip", this, toolBar);
        startButton = createButton("images/start16.png", null, "battle.start.tool.tip", this, toolBar);

        // Combatant table.
        CombatantTableModel model = new CombatantTableModel();
        combatantTable = new JTable(model);

        // Renderer and editor for button.
        combatantButtonColumn = new ButtonColumn(combatantTable, this);
        combatantTable.getColumnModel().getColumn(0).setCellRenderer(combatantButtonColumn);
        combatantTable.getColumnModel().getColumn(0).setCellEditor(combatantButtonColumn);

        // Renderer for action.
        JComboBox<BattleAction> actionComboBox = new JComboBox<>(BattleAction.values());
        DefaultCellEditor actionEditor = new DefaultCellEditor(actionComboBox);
        TableColumn actionColumn = combatantTable.getColumnModel().getColumn(7);
        actionColumn.setCellEditor(actionEditor);

        // Combatant table column widths.
        String columnWidthCsv = bundle.getString("combatant.table.column.widths");
        String[] columnWidths = StringUtils.split(columnWidthCsv, ',');
        for (int columnIndex = 0; columnIndex < columnWidths.length; columnIndex++) {
            TableColumn column = combatantTable.getColumnModel().getColumn(columnIndex);
            int columnWidth = Integer.parseInt(columnWidths[columnIndex]);
            column.setPreferredWidth(columnWidth);
        }

        // Make combatant table scrollable.
        JScrollPane combatantScrollable = makeScrollable(combatantTable, 100, 100);
        combatantPanel.add(combatantScrollable);

        // Battle logs.
        BattleLog log = new BattleLog(bundle.getString("battle.log.created"), null);
        List<BattleLog> battleLogs = battle.getBattleLogs();
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
        } else if (e.getSource().equals(addGroupButton)) {
            //addGroup();
        } else if (e.getSource().equals(startButton)) {
            //start();
        } else if (e.getSource().equals(combatantButtonColumn.getButton())) {
            removeChar();
        } else {
            LOGGER.debug("ActionEvent=\"" + e + "\"");
        }
    }

    private void addChar() {
        GameChar gameChar = showSelectCharDialog("images/character32.png", "battle.add.char.dialog.title",
                "battle.add.char.dialog.message", this);
        if (gameChar != null) {
            // Save current state of battle.
            // TODO Battle object will grow exponentially. Fix this soon!
            String battleJson = null;
            try {
                battleJson = mapper.writeValueAsString(battle);
            } catch (JsonProcessingException e) {
                throw new LoggingException(LOGGER, "Unable to convert to JSON: battle=\"" + battle + "\".");
            }

            // Get names for all combatants currently in the battle so the new combatant will have a different name.
            CombatantTableModel model = (CombatantTableModel) combatantTable.getModel();
            List<String> existingNames = new ArrayList<>();
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = (String) model.getValueAt(row, 1);
                existingNames.add(name);
            }
            Combatant combatant = new Combatant(gameChar, existingNames);

            // Add character to battle.
            ButtonDescriptor descriptor =
                    new ButtonDescriptor("images/remove8.png", null, "combatant.table.remove.char.tool.tip");
            CombatantTableRow row = new CombatantTableRow(combatant, descriptor);
            model.addRow(row);

            // Add log saying we added character.
            String message = String.format(bundle.getString("battle.log.char.added"), combatant.getName());
            BattleLog log = new BattleLog(message, battleJson);
            battle.getBattleLogs().add(log);
            DefaultListModel<BattleLog> logListModel = (DefaultListModel<BattleLog>) logList.getModel();
            logListModel.addElement(log);
        }
    }

    private void removeChar() {
        // Save current state of battle.
        // TODO Battle object will grow exponentially. Fix this soon!
        String battleJson = null;
        try {
            battleJson = mapper.writeValueAsString(battle);
        } catch (JsonProcessingException e) {
            throw new LoggingException(LOGGER, "Unable to convert to JSON: battle=\"" + battle + "\".");
        }

        // Remove character from battle.
        int rowIndex = combatantButtonColumn.getEditingRow();
        CombatantTableModel model = (CombatantTableModel) combatantTable.getModel();
        String name = (String) model.getValueAt(rowIndex, 1);
        model.removeRow(rowIndex);

        // Add log saying we removed character.
        String message = String.format(bundle.getString("battle.log.char.removed"), name);
        BattleLog log = new BattleLog(message, battleJson);
        battle.getBattleLogs().add(log);
        DefaultListModel<BattleLog> logListModel = (DefaultListModel<BattleLog>) logList.getModel();
        logListModel.addElement(log);
    }

    // COMMON METHODS // TODO Refactor these someday.

    private JButton createButton(String imagePath, String textKey, String toolTipKey, ActionListener listener,
            Container container) {
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
            container.add(button);
        }
        return button;
    }

    private JLabel createLabel(String imagePath, String textKey, Container container) {
        JLabel label = new JLabel();
        if (imagePath != null) {
            URL url = loader.getResource(imagePath);
            ImageIcon image = new ImageIcon(url);
            label.setIcon(image);
        }
        if (textKey != null) {
            String text = bundle.getString(textKey);
            label.setText(text);
        }
        if (container != null) {
            container.add(label);
        }
        return label;
    }

    private <T> JList<T> createList(List<T> elements, Container container) {
        DefaultListModel<T> model = new DefaultListModel<>();
        JList<T> list = new JList<>(model);
        if (elements != null) {
            for (T element : elements) {
                model.addElement(element);
            }
        }
        if (container != null) {
            container.add(list);
        }
        return list;
    }

    private JPanel createPanel(Container container) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        if (container != null) {
            container.add(panel);
        }
        return panel;
    }

    private JToolBar createToolBar(Container container) {
        JToolBar bar = new JToolBar();
        if (container != null) {
            container.add(bar, BorderLayout.NORTH);
        }
        return bar;
    }

    private JScrollPane makeScrollable(Component view, int width, int height) {
        JScrollPane scrollable = new JScrollPane(view);
        scrollable.setPreferredSize(new Dimension(width, height));
        return scrollable;
    }

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
