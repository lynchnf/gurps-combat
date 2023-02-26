package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleServiceTest {
    BattleService service;
    File tempDir;
    Battle testBattle;
    GameChar testGameChar;

    @BeforeEach
    void setUp() throws Exception {
        service = new BattleService(new ObjectMapper());

        // Override storage directory for testing.
        tempDir = Files.createTempDirectory("gurps-combat-temp-").toFile();
        ReflectionTestUtils.setField(service, "storageDir", tempDir);

        // Override storage file for testing.
        File tempFile = new File(tempDir, "battle.json");
        ReflectionTestUtils.setField(service, "storageBattleFile", tempFile);

        testBattle = new Battle();
        testBattle.getLogs().add(new BattleLog("Test Battle Log"));

        testGameChar = new GameChar();
        testGameChar.setName("Test Character Name");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
    }

    @Test
    void getBattleHappyPath() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "battle.json");
        mapper.writeValue(file, testBattle);

        Battle battle = service.getBattle();

        assertEquals(0, battle.getCombatants().size());
        assertNull(battle.getNextStep());
        assertEquals(1, battle.getLogs().size());
    }

    @Test
    void getBattleNotExist() {
        Battle battle = service.getBattle();

        assertNull(battle);
    }

    @Test
    void createBattleHappyPath() {
        service.createBattle();

        File file = new File(tempDir, "battle.json");
        assertTrue(file.exists());
    }

    @Test
    void createBattleAlreadyExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "battle.json");
        mapper.writeValue(file, testBattle);

        assertThrows(LoggingException.class, () -> {
            service.createBattle();
        });
    }

    @Test
    void deleteBattleHappyPath() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "battle.json");
        mapper.writeValue(file, testBattle);

        service.deleteBattle();
    }

    @Test
    void deleteBattleNotExist() {
        assertThrows(LoggingException.class, () -> {
            service.deleteBattle();
        });
    }

    @Test
    void addGameCharToBattle() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "battle.json");
        mapper.writeValue(file, testBattle);

        String label = service.addGameCharToBattle(testGameChar);

        assertEquals("Test Character Name", label);
    }

    @Test
    void removeCombatantFromBattleHappyPath() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "battle.json");
        Combatant combatant = new Combatant(testGameChar, new HashSet<String>());
        testBattle.getCombatants().put(combatant.getLabel(), combatant);
        mapper.writeValue(file, testBattle);

        service.removeCombatantFromBattle("Test Character Name");
    }

    @Test
    void removeCombatantFromBattleNotInBattle() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "battle.json");
        mapper.writeValue(file, testBattle);

        assertThrows(LoggingException.class, () -> {
            service.removeCombatantFromBattle("Test Character Name");
        });
    }
}