package norman.gurps.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.model.Battle;
import norman.gurps.model.BattleLog;
import norman.gurps.model.Combatant;
import norman.gurps.model.GameChar;
import norman.gurps.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
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
import javax.swing.table.TableCellRenderer;
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

public class OldBattleFrame extends JInternalFrame implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OldBattleFrame.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private ObjectMapper mapper;
    private Battle battle;
    private JButton addCharButton;
    private JButton addGroupButton;
    private JButton startButton;
    private JTable combatantTable;
    private JList<BattleLog> logList;

    public OldBattleFrame() {
        super();
        initComponents();
    }

    private void initComponents() {
        LOGGER.debug("Initializing battle frame.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.BattleFrame");
        loader = Thread.currentThread().getContextClassLoader();
        mapper = new ObjectMapper();
        setTitle(bundle.getString("battle.title"));
        setLayout(new BorderLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        battle = new Battle();

        // Battlefield map.
        JLabel mapLabel = createLabel("norman/gurps/gui/battlefield.png", null);
        JScrollPane mapScroll = makeScrollable(mapLabel, 200, 200);

        // Battle combatants.
        JPanel combatantPanel = createPanel(null);
        JToolBar toolBar = createToolBar(combatantPanel);
        addCharButton = createButton("norman/gurps/gui/character24.png", "battle.combatant.add.char", toolBar);
        addGroupButton = createButton("norman/gurps/gui/group24.png", "battle.combatant.add.group", toolBar);
        startButton = createButton("norman/gurps/gui/start24.png", "battle.combatant.start", toolBar);

        // Table columns names.
        String[] colNames =
                {"", "Name", "ST", "DX", "IQ", "HT", "Speed"}; // TODO Put this in a properties file somewhere.
        OldCombatantTableModel combatantModel = new OldCombatantTableModel(colNames);
        combatantTable = new JTable(combatantModel);

        // Table column widths.
        int[] colWidths = {20, 530, 50, 50, 50, 50, 50}; // TODO Put this in a properties file somewhere.
        for (int col = 0; col < colWidths.length; col++) {
            TableColumn column = combatantTable.getColumnModel().getColumn(col);
            column.setPreferredWidth(colWidths[col]);
        }

        // Special renderer for buttons.
        TableCellRenderer defaultRenderer = combatantTable.getDefaultRenderer(JButton.class);
        TableButtonRenderer buttonRenderer = new TableButtonRenderer(defaultRenderer);
        combatantTable.setDefaultRenderer(JButton.class, buttonRenderer);

        // Table scroll bars.
        JScrollPane combatantScroll = makeScrollable(combatantTable, 100, 100);
        combatantTable.setFillsViewportHeight(true); // Does this do anything?
        combatantPanel.add(combatantScroll);

        // Battle logs.
        BattleLog log = new BattleLog(bundle.getString("battle.log.created"), null);
        List<BattleLog> battleLogs = battle.getBattleLogs();
        battleLogs.add(log);
        logList = createList(battleLogs, null);
        JScrollPane logScroll = makeScrollable(logList, 50, 50);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, combatantPanel, logScroll);
        JSplitPane outerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapScroll, innerSplitPane);
        this.add(outerSplitPane);

        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(addCharButton)) {
            addChar();
        } else {
            LOGGER.debug("Unknown actionEvent=\"" + ((AbstractButton) actionEvent.getSource()).getText() + "\"");
        }
    }

    private void addChar() {
        GameChar choice = showSelectCharDialog("battle.combatant.add.char", "battle.combatant.add.char.message");
        if (choice != null) {
            // Save current state of battle.
            // TODO Battle object will grow exponentially. Fix this soon!
            String battleJson = null;
            try {
                battleJson = mapper.writeValueAsString(battle);
            } catch (JsonProcessingException e) {
                throw new LoggingException(LOGGER, "Unable to convert to JSON: battle=\"" + battle + "\".");
            }

            // Get names for all combatants currently in the battle so the new combatant will have a different name.
            OldCombatantTableModel model = (OldCombatantTableModel) combatantTable.getModel();
            List<String> existingNames = new ArrayList<>();
            for (int row = 0; row < model.getRowCount(); row++) {
                String name = (String) model.getValueAt(row, 1);
                existingNames.add(name);
            }
            Combatant combatant = new Combatant(choice, existingNames);

            // Add character to battle.
            JButton button = createButton("norman/gurps/gui/x8.png", "battle.combatant.remove.char", null);
            Object[] rowData = {button, combatant.getName(), combatant.getStrength(), combatant.getDexterity(),
                    combatant.getIntelligence(), combatant.getHealth(), combatant.getBasicSpeed()};
            model.addRow(rowData);

            // Add log saying we added character.
            String message = String.format(bundle.getString("battle.log.char.added"), combatant.getName());
            BattleLog log = new BattleLog(message, battleJson);
            battle.getBattleLogs().add(log);
            DefaultListModel<BattleLog> logListModel = (DefaultListModel<BattleLog>) logList.getModel();
            logListModel.addElement(log);
        }
    }

    // COMMON METHODS // TODO Refactor these someday.

    private GameChar showSelectCharDialog(String titleKey, String messageKey) {
        String title = bundle.getString(titleKey);
        Object message = bundle.getString(messageKey);
        List<GameChar> allGameChars = GameCharService.findAll();
        allGameChars.sort(Comparator.comparing(GameChar::getName));
        GameChar[] selectionValues = allGameChars.toArray(new GameChar[0]);
        return (GameChar) JOptionPane.showInternalInputDialog(this, message, title, JOptionPane.PLAIN_MESSAGE, null,
                selectionValues, null);
    }

    private JScrollPane makeScrollable(Component view, int width, int height) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setPreferredSize(new Dimension(width, height));
        return scroll;
    }

    private JPanel createPanel(Container container) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        if (container != null) {
            container.add(panel);
        }
        return panel;
    }

    private <T> JList<T> createList(List<T> elements, Container container) {
        DefaultListModel<T> model = new DefaultListModel<>();
        JList<T> list = new JList<>(model);
        for (T element : elements) {
            model.addElement(element);
        }
        if (container != null) {
            container.add(list);
        }
        return list;
    }

    private JToolBar createToolBar(Container container) {
        JToolBar bar = new JToolBar();
        if (container != null) {
            container.add(bar, BorderLayout.NORTH);
        }
        return bar;
    }

    private JLabel createLabel(String path, Container container) {
        JLabel label = new JLabel();
        URL url = loader.getResource(path);
        ImageIcon image = new ImageIcon(url);
        label.setIcon(image);
        if (container != null) {
            container.add(label);
        }
        return label;
    }

    private JButton createButton(String path, String key, Container container) {
        URL url = loader.getResource(path);
        ImageIcon icon = new ImageIcon(url);
        JButton button = new JButton(icon);
        button.setToolTipText(bundle.getString(key));
        button.addActionListener(this);
        if (container != null) {
            container.add(button);
        }
        return button;
    }
}
