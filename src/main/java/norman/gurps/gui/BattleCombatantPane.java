package norman.gurps.gui;

import norman.gurps.model.Combatant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BattleCombatantPane extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleCombatantPane.class);
    private ResourceBundle bundle;

    public BattleCombatantPane(List<Combatant> combatants) {
        super();
        initComponents(combatants);
    }

    private void initComponents(List<Combatant> combatants) {
        LOGGER.debug("Initializing battle combatant pane.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.BattleCombatantPane");
        setLayout(new BorderLayout());
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        URL addCharUrl = loader.getResource("norman/gurps/gui/character24.png");
        ImageIcon addCharIcon = new ImageIcon(addCharUrl);
        JButton addCharButton = new JButton(addCharIcon);
        addCharButton.setToolTipText(bundle.getString("battle.combatant.add.character"));

        URL addGroupUrl = loader.getResource("norman/gurps/gui/group24.png");
        ImageIcon addGroupIcon = new ImageIcon(addGroupUrl);
        JButton addGroupButton = new JButton(addGroupIcon);
        addGroupButton.setToolTipText(bundle.getString("battle.combatant.add.group"));

        URL startUrl = loader.getResource("norman/gurps/gui/start24.png");
        ImageIcon startIcon = new ImageIcon(startUrl);
        JButton startButton = new JButton(startIcon);
        startButton.setToolTipText(bundle.getString("battle.combatant.start"));

        JToolBar toolBar = new JToolBar();
        toolBar.add(addCharButton);
        toolBar.add(addGroupButton);
        toolBar.add(startButton);
        this.add(toolBar, BorderLayout.NORTH);

        JList<Combatant> combatantList = new JList<>(combatants.toArray(new Combatant[0]));
        this.add(combatantList);
    }
}
