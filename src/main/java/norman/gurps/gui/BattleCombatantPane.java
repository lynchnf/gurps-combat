package norman.gurps.gui;

import norman.gurps.model.Battle;
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
import java.util.List;
import java.util.ResourceBundle;

public class BattleCombatantPane extends JPanel implements ActionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleCombatantPane.class);
    private ResourceBundle bundle;
    private ClassLoader loader;
    private JButton addCharButton;
    private JButton addGroupButton;
    private JButton startButton;

    public BattleCombatantPane(Battle battle) {
        super();
        initComponents(battle);
    }

    private void initComponents(Battle battle) {
        LOGGER.debug("Initializing battle combatant pane.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.BattleCombatantPane");
        loader = Thread.currentThread().getContextClassLoader();
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH);

        addCharButton = createButton("norman/gurps/gui/character24.png", "battle.combatant.add.char", toolBar);
        addGroupButton = createButton("norman/gurps/gui/group24.png", "battle.combatant.add.group", toolBar);
        startButton = createButton("norman/gurps/gui/start24.png", "battle.combatant.start", toolBar);

        JList<Combatant> combatantList = new JList<>(battle.getCombatants().toArray(new Combatant[0]));
        this.add(combatantList);
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
            //showCharEditFrame(choice);
        }
    }

    private GameChar showSelectCharDialog(String titleKey, String messageKey) {
        String title = bundle.getString(titleKey);
        Object message = bundle.getString(messageKey);
        List<GameChar> allGameChars = GameCharService.findAll();
        GameChar[] selectionValues = allGameChars.toArray(new GameChar[0]);
        return (GameChar) JOptionPane.showInternalInputDialog(this, message, title, JOptionPane.PLAIN_MESSAGE, null,
                selectionValues, null);
    }


    // COMMON METHODS // TODO Refactor these someday.

    private JButton createButton(String path, String key, Container container) {
        URL url = loader.getResource(path);
        ImageIcon icon = new ImageIcon(url);
        JButton button = new JButton(icon);
        button.setToolTipText(bundle.getString(key));
        button.addActionListener(this);
        container.add(button);
        return button;
    }
}
