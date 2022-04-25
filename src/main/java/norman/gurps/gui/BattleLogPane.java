package norman.gurps.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.model.Battle;
import norman.gurps.model.BattleLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

public class BattleLogPane extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleLogPane.class);
    private ResourceBundle bundle;
    private ObjectMapper mapper;

    public BattleLogPane(Battle battle) {
        super();
        initComponents(battle);
    }

    private void initComponents(Battle battle) {
        LOGGER.debug("Initializing battle log pane.");
        bundle = ResourceBundle.getBundle("norman.gurps.gui.BattleLogPane");
        mapper = new ObjectMapper();
        setLayout(new BorderLayout());

        String message = bundle.getString("battle.log.created");
        String battleJson = "{}";
        try {
            battleJson = mapper.writeValueAsString(battle);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Uable to convert to JSON: battle=\"" + battle + "\".");
        }
        BattleLog log = new BattleLog(message, battleJson);
        List<BattleLog> battleLogs = battle.getBattleLogs();
        battleLogs.add(log);

        JList<BattleLog> logList = new JList<>(battleLogs.toArray(new BattleLog[0]));
        this.add(logList);
    }
}
