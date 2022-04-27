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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
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
    private JList<Combatant> combatantList;
    private JList<BattleLog> logList;

    public BattleFrame() {
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
        JScrollPane combatantScroll = makeScrollable(combatantPanel, 100, 100);
        JToolBar toolBar = createToolBar(combatantPanel);
        addCharButton = createButton("norman/gurps/gui/character24.png", "battle.combatant.add.char", toolBar);
        addGroupButton = createButton("norman/gurps/gui/group24.png", "battle.combatant.add.group", toolBar);
        startButton = createButton("norman/gurps/gui/start24.png", "battle.combatant.start", toolBar);
        combatantList = createList(battle.getCombatants(), combatantPanel);

        // Battle logs.
        BattleLog log = new BattleLog(bundle.getString("battle.log.created"), null);
        List<BattleLog> battleLogs = battle.getBattleLogs();
        battleLogs.add(log);
        logList = createList(battleLogs, null);
        JScrollPane logScroll = makeScrollable(logList, 50, 50);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, combatantScroll, logScroll);
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

            // Add character.
            Combatant combatant = new Combatant(choice);
            battle.getCombatants().add(combatant);
            DefaultListModel<Combatant> combatantListModel = (DefaultListModel<Combatant>) combatantList.getModel();
            combatantListModel.addElement(combatant);

            // Add log saying we added character.
            String message = String.format(bundle.getString("battle.log.char.added"), choice.toString());
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
