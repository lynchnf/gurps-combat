package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.AddStoredCharacterToCurrentBattleRequest;
import norman.gurps.combat.controller.request.RemoveCombatantFromCurrentBattle;
import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowBattleResponse;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.BattleService;
import norman.gurps.combat.service.GameCharService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BattleControllerTest {
    BattleController controller;
    BattleService service;
    GameCharService gameCharService;
    GameChar testGameChar;
    Battle testBattle;

    @BeforeEach
    void setUp() {
        service = mock(BattleService.class);
        gameCharService = mock(GameCharService.class);
        controller = new BattleController(service, gameCharService);

        testGameChar = new GameChar();
        testGameChar.setName("Test Character Name");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);

        testBattle = new Battle();
        testBattle.getLogs().add(new BattleLog("Test Battle Log"));
    }

    @Test
    void createEmptyBattleHappyPath() {
        CombatResponse resp = controller.createEmptyBattle();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void createEmptyBattleAlreadyExist() {
        when(service.getBattle()).thenReturn(testBattle);

        CombatResponse resp = controller.createEmptyBattle();

        assertFalse(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void deleteCurrentBattleHappyPath() {
        when(service.getBattle()).thenReturn(testBattle);

        CombatResponse resp = controller.deleteCurrentBattle();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void deleteCurrentBattleNotFound() {
        CombatResponse resp = controller.deleteCurrentBattle();

        assertFalse(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void addStoredCharacterToCurrentBattle() {
        AddStoredCharacterToCurrentBattleRequest req = new AddStoredCharacterToCurrentBattleRequest();
        req.setName("Test Character Name");
        Map<String, GameChar> gameCharMap = new HashMap<>();
        gameCharMap.put(testGameChar.getName(), testGameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameCharMap);
        when(service.getBattle()).thenReturn(testBattle);

        CombatResponse resp = controller.addStoredCharacterToCurrentBattle(req);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void removeCombatantFromCurrentBattleHappyPath() {
        RemoveCombatantFromCurrentBattle req = new RemoveCombatantFromCurrentBattle();
        req.setLabel("Test Character Name");
        when(service.getBattle()).thenReturn(testBattle);
        Combatant combatant = new Combatant(testGameChar, new HashSet<>());
        testBattle.getCombatants().put(combatant.getLabel(), combatant);

        CombatResponse resp = controller.removeCombatantFromCurrentBattle(req);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void removeCombatantFromCurrentBattleNotExist() {
        RemoveCombatantFromCurrentBattle req = new RemoveCombatantFromCurrentBattle();
        req.setLabel("Test Character Name");

        CombatResponse resp = controller.removeCombatantFromCurrentBattle(req);

        assertFalse(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void showBattle() {
        when(service.getBattle()).thenReturn(testBattle);

        ShowBattleResponse resp = controller.showBattle();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
        assertEquals(0, resp.getBattle().getCombatants().size());
        assertEquals(1, resp.getBattle().getLogs().size());
    }
}