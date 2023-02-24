package norman.gurps.combat.service;

import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameCharServiceTest {
    GameCharService service;

    @BeforeEach
    void setUp() {
        service = new GameCharService();
    }

    @Test
    void validateHappyPath() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertTrue(errors.isEmpty());
    }

    @Test
    void validateBadName() {
        GameChar gameChar = new GameChar();
        gameChar.setName(" ");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoStrength() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadStrength() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(-4);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoDexterity() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadDexterity() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(-3);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoIntelligence() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadIntelligence() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(-2);
        gameChar.setHealth(11);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateNoHealth() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void validateBadHealth() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        gameChar.setHealth(-1);
        List<String> errors = service.validate(gameChar);
        assertFalse(errors.isEmpty());
    }

    @Test
    void getStoredGameChars() {
    }

    @Test
    void saveStoredGameChars() {
    }
}