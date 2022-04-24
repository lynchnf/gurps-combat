package norman.gurps.gui;

import norman.gurps.model.BattleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BattleLogPane extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleLogPane.class);

    public BattleLogPane(List<BattleLog> battleLogs) {
        super();
        initComponents(battleLogs);
    }

    private void initComponents(List<BattleLog> battleLogs) {
        LOGGER.debug("Initializing battle log pane.");
        setLayout(new BorderLayout());

        JList<BattleLog> logList = new JList<>(battleLogs.toArray(new BattleLog[0]));
        this.add(logList);
    }
}
