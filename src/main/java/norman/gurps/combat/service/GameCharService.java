package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.GameChar;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameCharService {
    private static Logger LOGGER = LoggerFactory.getLogger(GameCharService.class);
    private static final String STORAGE_DIR_NAME = ".gurps-combat";
    private static final String STORAGE_GAME_CHAR_FILE_NAME = "game-char.json";
    private ObjectMapper mapper;
    private File storageDir;

    @Autowired
    public GameCharService(ObjectMapper mapper) {
        this.mapper = mapper;
        storageDir = new File(SystemUtils.USER_HOME, STORAGE_DIR_NAME);
    }

    // This method is just used to make testing easier.
    protected void setStorageDir(File storageDir) {
        this.storageDir = storageDir;
    }

    public List<String> validate(GameChar gameChar) {
        List<String> errors = new ArrayList<>();
        if (StringUtils.trimToNull(gameChar.getName()) == null) {
            errors.add("Name may not be blank.");
        }
        if (gameChar.getStrength() == null) {
            errors.add("Strength may not be blank.");
        } else if (gameChar.getStrength() < 0) {
            errors.add("Strength may not be less than zero.");
        }
        if (gameChar.getDexterity() == null) {
            errors.add("Dexterity may not be blank.");
        } else if (gameChar.getDexterity() < 0) {
            errors.add("Dexterity may not be less than zero.");
        }
        if (gameChar.getIntelligence() == null) {
            errors.add("Intelligence may not be blank.");
        } else if (gameChar.getIntelligence() < 0) {
            errors.add("Intelligence may not be less than zero.");
        }
        if (gameChar.getHealth() == null) {
            errors.add("Health may not be blank.");
        } else if (gameChar.getHealth() < 0) {
            errors.add("Health may not be less than zero.");
        }
        return errors;
    }

    public Map<String, GameChar> getStoredGameChars() {
        // Create home directory if it does not exist.
        if (!storageDir.exists()) {
            LOGGER.debug("Creating storage directory " + storageDir + ".");
            if (!storageDir.mkdirs()) {
                throw new LoggingException(LOGGER, "Unable to create storage directory " + storageDir + ".");
            }
        }

        // Load stored chars file. Create it if it does not already exist.
        File storageGameCharFile = new File(storageDir, STORAGE_GAME_CHAR_FILE_NAME);
        Map<String, GameChar> gameCharMap = new HashMap<>();
        if (storageGameCharFile.exists()) {
            LOGGER.debug("Loading stored game chars.");
            try {
                GameChar[] gameCharArray = mapper.readValue(storageGameCharFile, GameChar[].class);
                List<GameChar> gameCharList = new ArrayList<>();
                gameCharList.addAll(Arrays.asList(gameCharArray));
                for (GameChar gameChar : gameCharList) {
                    gameCharMap.put(gameChar.getName(), gameChar);
                }
            } catch (IOException e) {
                throw new LoggingException(LOGGER,
                        "Error loading stored game chars file from " + storageGameCharFile + ".", e);
            }
        } else {
            LOGGER.debug("Saving new stored game chars file.");
            saveStoredGameChars(gameCharMap);
        }

        return gameCharMap;
    }

    public void saveStoredGameChars(Map<String, GameChar> gameChars) {
        LOGGER.debug("Storing game chars to local storage.");
        File storageGameCharFile = new File(storageDir, STORAGE_GAME_CHAR_FILE_NAME);
        try {
            mapper.writeValue(storageGameCharFile, gameChars.values());
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error storing game chars to file " + storageGameCharFile + ".", e);
        }
    }
}
