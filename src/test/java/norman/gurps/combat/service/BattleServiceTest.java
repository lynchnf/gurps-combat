package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BattleServiceTest {
    BattleService service;
    GameCharService gameCharService;
    File tempDir;
    File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        gameCharService = mock(GameCharService.class);
        service = new BattleService(new ObjectMapper(), gameCharService);

        // Override storage directory for testing.
        tempDir = Files.createTempDirectory("gurps-combat-temp-").toFile();
        ReflectionTestUtils.setField(service, "storageDir", tempDir);

        // Override storage file for testing.
        tempFile = new File(tempDir, "battle.json");
        ReflectionTestUtils.setField(service, "storageBattleFile", tempFile);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createBattle() throws Exception {
        service.createBattle();

        assertTrue(tempFile.exists());
        ObjectMapper mapper = new ObjectMapper();
        Battle battle = mapper.readValue(tempFile, Battle.class);
        assertEquals(0, battle.getCombatants().size());
        assertNull(battle.getNextStep());
        assertEquals(1, battle.getLogs().size());
    }

    @Test
    void deleteBattle() throws Exception {
        Battle battle = new Battle();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        service.deleteBattle();

        assertFalse(tempFile.exists());
    }

    @Test
    void addCharToBattle_empty_battle() throws Exception {
        // Mock Game Character service.
        List<GameChar> gameChars = new ArrayList<>();
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character");
        gameChars.add(gameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameChars);

        // Create empty battle in storage.
        ObjectMapper mapper = new ObjectMapper();
        Battle battle = new Battle();
        battle.getLogs().add(new BattleLog("Battle created."));
        mapper.writeValue(tempFile, battle);

        String label = service.addCharToBattle("Test Character");

        assertEquals("Test Character", label);
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(1, battle1.getCombatants().size());
        assertEquals("Test Character", battle1.getCombatants().get(0).getLabel());
        assertEquals("Test Character", battle1.getCombatants().get(0).getGameChar().getName());
        assertNull(battle1.getNextStep());
        assertEquals(2, battle1.getLogs().size());
    }

    @Test
    void addCharToBattle_char_already_in_battle() throws Exception {
        // Mock Game Character service.
        List<GameChar> gameChars = new ArrayList<>();
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character");
        gameChars.add(gameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameChars);

        // Create battle in storage with this char.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getLogs().add(new BattleLog("Battle created."));
        battle.getLogs().add(new BattleLog("Combatant Test Character added to Battle."));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        String label = service.addCharToBattle("Test Character");

        assertEquals("Test Character 2", label);
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(2, battle1.getCombatants().size());
        assertEquals("Test Character 2", battle1.getCombatants().get(1).getLabel());
        assertEquals("Test Character", battle1.getCombatants().get(1).getGameChar().getName());
        assertNull(battle1.getNextStep());
        assertEquals(3, battle1.getLogs().size());
    }

    @Test
    void removeCharFromBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        GameChar gameChar = new GameChar();
        String name = "Test Character";
        gameChar.setName(name);
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getLogs().add(new BattleLog("Battle created."));
        battle.getLogs().add(new BattleLog("Combatant Test Character added to Battle."));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);
        String label = name;

        service.removeCharFromBattle(label);

        // Verify combatant no longer exists in battle.
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(0, battle1.getCombatants().size());
        assertNull(battle1.getNextStep());
        assertEquals(3, battle1.getLogs().size());
    }

    @Test
    void getBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        GameChar gameChar = new GameChar();
        String name = "Test Character";
        gameChar.setName(name);
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getLogs().add(new BattleLog("Test Log"));
        battle.getLogs().add(new BattleLog("Another Test Log"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        assertEquals(1, battle.getCombatants().size());
        assertNull(battle.getNextStep());
        assertEquals(2, battle.getLogs().size());
    }
}