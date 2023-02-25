package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameCharServiceTest {
    GameCharService service;
    File tempDir;
    GameChar testGameChar;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        service = new GameCharService(mapper);

        // Override storage directory for testing.
        tempDir = Files.createTempDirectory("gurps-combat-temp-").toFile();
        ReflectionTestUtils.setField(service, "storageDir", tempDir);

        // Override storage file for testing.
        ReflectionTestUtils.setField(service, "storageGameCharFileName", "game-char.json");

        testGameChar = new GameChar();
        testGameChar.setName("Test Character Name");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
    }

    @Test
    void validateHappyPath() {
        List<String> errors = service.validate(testGameChar);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateBadName() {
        testGameChar.setName(" ");

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoStrength() {
        testGameChar.setStrength(null);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadStrength() {
        testGameChar.setStrength(-4);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoDexterity() {
        testGameChar.setDexterity(null);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadDexterity() {
        testGameChar.setDexterity(-3);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoIntelligence() {
        testGameChar.setIntelligence(null);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadIntelligence() {
        testGameChar.setIntelligence(-2);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoHealth() {
        testGameChar.setHealth(null);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadHealth() {
        testGameChar.setHealth(-1);

        List<String> errors = service.validate(testGameChar);

        assertFalse(errors.isEmpty());
    }

    @Test
    void getStoredGameChars() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        List<GameChar> gameCharList = new ArrayList<>();
        gameCharList.add(testGameChar);
        mapper.writeValue(file, gameCharList);

        Map<String, GameChar> gameCharMap = service.getStoredGameChars();

        assertEquals(1, gameCharMap.size());
        assertTrue(gameCharMap.containsKey("Test Character Name"));
        assertEquals(gameCharMap.get("Test Character Name").getName(), "Test Character Name");
        assertEquals(gameCharMap.get("Test Character Name").getStrength(), 14);
        assertEquals(gameCharMap.get("Test Character Name").getDexterity(), 13);
        assertEquals(gameCharMap.get("Test Character Name").getIntelligence(), 12);
        assertEquals(gameCharMap.get("Test Character Name").getHealth(), 11);
    }

    @Test
    void saveStoredGameChars() throws Exception {
        Map<String, GameChar> gameCharMap = new HashMap<>();
        gameCharMap.put(testGameChar.getName(), testGameChar);

        service.saveStoredGameChars(gameCharMap);

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        GameChar[] gameCharArray = mapper.readValue(file, GameChar[].class);
        assertEquals(1, gameCharArray.length);
        assertEquals(gameCharArray[0].getName(), "Test Character Name");
        assertEquals(gameCharArray[0].getStrength(), 14);
        assertEquals(gameCharArray[0].getDexterity(), 13);
        assertEquals(gameCharArray[0].getIntelligence(), 12);
        assertEquals(gameCharArray[0].getHealth(), 11);
    }
}