package norman.gurps.combat.service;

import norman.gurps.combat.model.GameChar;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameCharService {
    private static Logger LOGGER = LoggerFactory.getLogger(GameCharService.class);
    private static final String APP_DIR_NAME = ".gurps-combat";
    private static final String APP_PROPS_FILE_NAME = "gurps-combat.json";

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
        Map<String, GameChar> gameChars = new HashMap<>();
        return gameChars;
    }

    public void saveStoredGameChars(Map<String, GameChar> gameChars) {
    }
}
