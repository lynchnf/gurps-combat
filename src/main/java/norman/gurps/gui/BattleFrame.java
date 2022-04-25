package norman.gurps.gui;

import norman.gurps.model.Battle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class BattleFrame extends JInternalFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleFrame.class);
    private ResourceBundle bundle;

    public BattleFrame() {
        super();
        initComponents();
    }

    private void initComponents() {
        LOGGER.debug("Initializing battle frame.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.BattleFrame");
        setTitle(bundle.getString("battle.title"));
        setLayout(new BorderLayout());
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        Battle battle = new Battle();

        // Battlefield map.
        String path = "norman/gurps/gui/battlefield.png";
        int width = 200;
        int height = 200;
        BattleMapPane mapPane = new BattleMapPane(path, width, height);

        // Battle combatants.
        BattleCombatantPane combatantPane = new BattleCombatantPane(battle);

        // Battle logs.
        BattleLogPane logPane = new BattleLogPane(battle);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, combatantPane, logPane);
        JSplitPane outerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPane, innerSplitPane);
        this.add(outerSplitPane);

        pack();
        setVisible(true);
    }
}
