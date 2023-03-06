package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BattleService {
    private static Logger LOGGER = LoggerFactory.getLogger(BattleService.class);
    @Value("${storage.dir.name}")
    private String storageDirName;
    @Value("${storage.battle.file.name}")
    private String storageBattleFileName;
    private ObjectMapper mapper;
    private GameCharService gameCharService;
    private File storageDir;
    private File storageBattleFile;

    public BattleService(ObjectMapper mapper, GameCharService gameCharService) {
        this.mapper = mapper;
        this.gameCharService = gameCharService;
    }

    @PostConstruct
    private void postConstruct() {
        storageDir = new File(SystemUtils.USER_HOME, storageDirName);
        storageBattleFile = new File(storageDir, storageBattleFileName);
    }

    public void createBattle() {
        // Create home directory if it does not exist.
        verifyStorageDir();

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
        verifyStorageDir();

        // Verify battle file is not already missing.
        if (!storageBattleFile.exists()) {
            throw new LoggingException(LOGGER, "Battle could not be deleted. It does not exist.");
        }

        // Delete battle file.
        if (!storageBattleFile.delete()) {
            throw new LoggingException(LOGGER, "Battle file " + storageBattleFile + " could not be deleted.");
        }
    }

    public String addCharToBattle(String name) {
        // Verify a good game char name was passed in.
        if (StringUtils.isBlank(name)) {
            throw new LoggingException(LOGGER, "Invalid Game Character. Name may not be blank.");
        }

        List<GameChar> storedGameChars = gameCharService.getStoredGameChars();
        GameChar foundGameChar = null;
        for (GameChar gameChar : storedGameChars) {
            if (gameChar.getName().equals(name)) {
                foundGameChar = gameChar;
            }
        }

        if (foundGameChar == null) {
            throw new LoggingException(LOGGER, "Game Character " + name + " not found in local storage.");
        }

        Battle battle = getBattle();
        Set<String> combatantLabels = new HashSet<>();
        for (Combatant combatant : battle.getCombatants()) {
            combatantLabels.add(combatant.getLabel());
        }
        Combatant newCombatant = new Combatant(foundGameChar, combatantLabels);
        battle.getCombatants().add(newCombatant);
        battle.getLogs().add(new BattleLog("Combatant " + newCombatant.getLabel() + " added to Battle."));
        try {
            mapper.writeValue(storageBattleFile, battle);
            return newCombatant.getLabel();
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error writing battle to file " + storageBattleFile + ".", e);
        }
    }

    public void removeCharFromBattle(String label) {
        // Verify a good game char name was passed in.
        if (StringUtils.isBlank(label)) {
            throw new LoggingException(LOGGER, "Invalid Combatant. Label may not be blank.");
        }

        Battle battle = getBattle();
        Combatant foundCombatant = null;
        for (Combatant combatant : battle.getCombatants()) {
            if (combatant.getLabel().equals(label)) {
                foundCombatant = combatant;
            }
        }

        if (foundCombatant == null) {
            throw new LoggingException(LOGGER, "Combatant " + label + " not found in Battle.");
        }
        battle.getCombatants().remove(foundCombatant);
        battle.getLogs().add(new BattleLog("Combatant " + foundCombatant.getLabel() + " removed from Battle."));
        try {
            mapper.writeValue(storageBattleFile, battle);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error writing battle to file " + storageBattleFile + ".", e);
        }
    }

    public Battle getBattle() {
        // Create home directory if it does not exist.
        verifyStorageDir();

        // Verify battle file exists.
        if (!storageBattleFile.exists()) {
            throw new LoggingException(LOGGER, "Battle does not exist.");
        }

        try {
            return mapper.readValue(storageBattleFile, Battle.class);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error loading Battle file from " + storageBattleFile + ".", e);
        }
    }

    private void verifyStorageDir() {
        if (!storageDir.exists()) {
            LOGGER.debug("Creating storage directory " + storageDir + ".");
            if (!storageDir.mkdirs()) {
                throw new LoggingException(LOGGER, "Unable to create storage directory " + storageDir + ".");
            }
        }
    }

    public void updateBattle(Battle battle, String message) {
        // Create home directory if it does not exist.
        verifyStorageDir();

        // Verify battle file is not missing.
        if (!storageBattleFile.exists()) {
            throw new LoggingException(LOGGER, "Battle could not be updated. It does not exist.");
        }

        battle.getLogs().add(new BattleLog(message));
        try {
            mapper.writeValue(storageBattleFile, battle);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error updating battle in file " + storageBattleFile + ".", e);
        }
    }
}
