package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
public class BattleService {
    private static Logger LOGGER = LoggerFactory.getLogger(BattleService.class);
    @Value("${storage.dir.name}")
    private String storageDirName;
    @Value("${storage.battle.file.name}")
    private String storageBattleFileName;
    private ObjectMapper mapper;
    private File storageDir;
    private File storageBattleFile;

    public BattleService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    private void postConstruct() {
        storageDir = new File(SystemUtils.USER_HOME, storageDirName);
        storageBattleFile = new File(storageDir, storageBattleFileName);
    }

    public Battle getBattle() {
        if (!storageDir.exists() || !storageBattleFile.exists()) {
            return null;
        }
        try {
            return mapper.readValue(storageBattleFile, Battle.class);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error reading battle from file " + storageBattleFile + ".", e);
        }
    }

    public void createBattle() {
        // Create home directory if it does not exist.
        if (!storageDir.exists()) {
            LOGGER.debug("Creating storage directory " + storageDir + ".");
            if (!storageDir.mkdirs()) {
                throw new LoggingException(LOGGER, "Unable to create storage directory " + storageDir + ".");
            }
        }

        // Verify battle file does not already exist.
        if (storageBattleFile.exists()) {
            throw new LoggingException(LOGGER, "Battle could not be created. It already exists.");
        }

        // Write new battle file.
        Battle battle = new Battle();
        battle.getLogs().add(new BattleLog("Battle created."));
        try {
            mapper.writeValue(storageBattleFile, battle);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error writing battle to file " + storageBattleFile + ".", e);
        }
    }

    public void deleteBattle() {
        // Create home directory if it does not exist.
        if (!storageDir.exists()) {
            LOGGER.debug("Creating storage directory " + storageDir + ".");
            if (!storageDir.mkdirs()) {
                throw new LoggingException(LOGGER, "Unable to create storage directory " + storageDir + ".");
            }
        }

        // Verify battle file is missing.
        if (!storageBattleFile.exists()) {
            throw new LoggingException(LOGGER, "Battle could not be deleted. It does not exist.");
        }

        // delete battle file.
        if (!storageBattleFile.delete()) {
            throw new LoggingException(LOGGER, "Battle file " + storageBattleFile + " could not be deleted.");
        }
    }

    public String addGameCharToBattle(GameChar gameChar) {
        Battle battle = getBattle();
        if (battle == null) {
            throw new LoggingException(LOGGER, "Battle does not currently exist.");
        }
        Combatant combatant = new Combatant(gameChar, battle.getCombatants().keySet());
        battle.getCombatants().put(combatant.getLabel(), combatant);
        battle.getLogs().add(new BattleLog("Combatant " + combatant.getLabel() + " added to battle."));
        try {
            mapper.writeValue(storageBattleFile, battle);
            return combatant.getLabel();
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error writing battle to file " + storageBattleFile + ".", e);
        }
    }

    public void removeCombatantFromBattle(String label) {
        Battle battle = getBattle();
        if (battle == null) {
            throw new LoggingException(LOGGER, "Battle does not currently exist.");
        }
        if (!battle.getCombatants().containsKey(label)) {
            throw new LoggingException(LOGGER, "Combatant " + label + " not found in current battle.");
        }
        battle.getCombatants().remove(label);
        battle.getLogs().add(new BattleLog("Combatant " + label + " removed from battle."));
        try {
            mapper.writeValue(storageBattleFile, battle);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error writing battle to file " + storageBattleFile + ".", e);
        }
    }
}
