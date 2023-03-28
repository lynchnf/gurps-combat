package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.NextStep;
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
    GameChar gameChar;

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

        gameChar = TestHelper.getGameChar1();
    }

    @Test
    void createBattle() throws Exception {
        service.createBattle();

        assertTrue(tempFile.exists());
        ObjectMapper mapper = new ObjectMapper();
        Battle battle = mapper.readValue(tempFile, Battle.class);
        assertEquals(0, battle.getCombatants().size());
        assertNull(battle.getNextStep());
        assertEquals(1, battle.getBattleLogs().size());
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
        gameChars.add(gameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameChars);

        // Create empty battle in storage.
        ObjectMapper mapper = new ObjectMapper();
        Battle battle = new Battle();
        battle.getBattleLogs().add(new BattleLog("Battle created."));
        mapper.writeValue(tempFile, battle);

        String label = service.addCharToBattle("Bob the Example");

        assertEquals("Bob the Example", label);
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(1, battle1.getCombatants().size());
        assertEquals("Bob the Example", battle1.getCombatants().get(0).getLabel());
        assertEquals("Bob the Example", battle1.getCombatants().get(0).getGameChar().getName());
        assertNull(battle1.getNextStep());
        assertEquals(2, battle1.getBattleLogs().size());
    }

    @Test
    void addCharToBattle_char_already_in_battle() throws Exception {
        // Mock Game Character service.
        List<GameChar> gameChars = new ArrayList<>();
        gameChars.add(gameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameChars);

        // Create battle in storage with this char.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getBattleLogs().add(new BattleLog("Battle created."));
        battle.getBattleLogs().add(new BattleLog("Combatant Test Character added to Battle."));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        String label = service.addCharToBattle("Bob the Example");

        assertEquals("Bob the Example 2", label);
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(2, battle1.getCombatants().size());
        assertEquals("Bob the Example 2", battle1.getCombatants().get(1).getLabel());
        assertEquals("Bob the Example", battle1.getCombatants().get(1).getGameChar().getName());
        assertNull(battle1.getNextStep());
        assertEquals(3, battle1.getBattleLogs().size());
    }

    @Test
    void removeCharFromBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getBattleLogs().add(new BattleLog("Battle created."));
        battle.getBattleLogs().add(new BattleLog("Combatant Test Character added to Battle."));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        service.removeCharFromBattle("Bob the Example");

        // Verify combatant no longer exists in battle.
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(0, battle1.getCombatants().size());
        assertNull(battle1.getNextStep());
        assertEquals(3, battle1.getBattleLogs().size());
    }

    @Test
    void getBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getBattleLogs().add(new BattleLog("Test Log"));
        battle.getBattleLogs().add(new BattleLog("Another Test Log"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        Battle battle1 = service.getBattle();

        assertEquals(1, battle1.getCombatants().size());
        assertNull(battle1.getNextStep());
        assertEquals(2, battle1.getBattleLogs().size());
    }

    @Test
    void updateBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(gameChar, existingLabels);
        battle.getCombatants().add(combatant);
        NextStep nextStep = new NextStep();
        nextStep.setRound(1);
        nextStep.setIndex(2);
        nextStep.setCombatPhase(CombatPhase.END_TURN);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("Test Message");
        battle.setNextStep(nextStep);
        battle.getBattleLogs().add(new BattleLog("Test Log 1"));
        battle.getBattleLogs().add(new BattleLog("Test Log 2"));
        battle.getBattleLogs().add(new BattleLog("Test Log 3"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        // Get, then change current battle.
        Battle battle1 = service.getBattle();
        battle1.getCombatants().get(0).setCurrentDamage(4);
        battle1.getNextStep().setRound(2);
        battle1.getNextStep().setIndex(3);
        battle1.getNextStep().setCombatPhase(CombatPhase.BEGIN_TURN);
        battle1.getNextStep().setInputNeeded(false);
        battle1.getNextStep().setMessage("Different Test Message");

        service.updateBattle(battle1, "Another Test Log");

        // Verify battle in storage is updated.
        Battle battle2 = mapper.readValue(tempFile, Battle.class);
        assertEquals(4, (int) battle2.getCombatants().get(0).getCurrentDamage());
        assertEquals(2, (int) battle2.getNextStep().getRound());
        assertEquals(3, (int) battle2.getNextStep().getIndex());
        assertEquals(CombatPhase.BEGIN_TURN, battle2.getNextStep().getCombatPhase());
        assertFalse(battle2.getNextStep().getInputNeeded());
        assertEquals("Different Test Message", battle2.getNextStep().getMessage());
        assertEquals(4, battle2.getBattleLogs().size());
    }
}
