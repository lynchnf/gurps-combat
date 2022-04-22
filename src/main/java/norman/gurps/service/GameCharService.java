package norman.gurps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.model.GameChar;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameCharService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCharService.class);
    private static final String APP_DIR_NAME = ".gurps-combat";
    private static final String APP_CHAR_FILE_NAME = "characters.json";
    private static ObjectMapper mapper = new ObjectMapper();

    public static void save(GameChar gameChar) {
        // Get all the old characters from the json file.
        List<GameChar> oldGameChars = loadGameChars();

        // If the character to be saved is new, ...
        int newGameCharId = gameChar.getId();
        if (newGameCharId < 0) {
            // Find the highest existing id.
            int maxId = 0;
            for (GameChar oldGameChar : oldGameChars) {
                int oldGameCharId = oldGameChar.getId();
                if (maxId < oldGameCharId) {
                    maxId = oldGameCharId;
                }
            }
            // Add one to the highest existing id and that's the id of our new character.
            gameChar.setId(maxId + 1);
        } else {
            // Otherwise, the character to be saved already exists.
            for (GameChar oldGameChar : oldGameChars) {
                // Find and remove the old version.
                int oldGameCharId = oldGameChar.getId();
                if (newGameCharId == oldGameCharId) {
                    oldGameChars.remove(oldGameChar);
                }
            }
        }

        // Add the character to be saved to our json file.
        oldGameChars.add(gameChar);
        storeGameChars(oldGameChars);
        LOGGER.debug("Successfully saved " + gameChar + ".");
    }

    private static List<GameChar> loadGameChars() {
        List<GameChar> gameCharList = new ArrayList<>();

        // Create home directory if it does not exist.
        File appDir = new File(SystemUtils.USER_HOME, APP_DIR_NAME);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        // Load characters file. Create it if it does not already exist.
        File appCharFile = new File(appDir, APP_CHAR_FILE_NAME);
        if (appCharFile.exists()) {
            try {
                GameChar[] gameCharArray = mapper.readValue(appCharFile, GameChar[].class);
                gameCharList.addAll(Arrays.asList(gameCharArray));
            } catch (IOException e) {
                throw new LoggingException(LOGGER, "Error loading character data file from " + appCharFile + ".", e);
            }
        } else {
            storeGameChars(gameCharList);
        }
        return gameCharList;
    }

    private static void storeGameChars(List<GameChar> gameChars) {
        File appDir = new File(SystemUtils.USER_HOME, APP_DIR_NAME);
        File appCharFile = new File(appDir, APP_CHAR_FILE_NAME);
        try {
            mapper.writeValue(appCharFile, gameChars);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error storing character data file to " + appCharFile + ".", e);
        }
    }
}
