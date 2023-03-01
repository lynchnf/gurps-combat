package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowBattleResponse;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BattleControllerTest {
    BattleController controller;
    BattleService service;

    @BeforeEach
    void setUp() {
        service = mock(BattleService.class);
        controller = new BattleController(service);
    }

    @Test
    void createEmptyBattle() {
        CombatResponse resp = controller.createEmptyBattle();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void deleteCurrentBattle() {
        CombatResponse resp = controller.deleteCurrentBattle();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void addStoredCharacterToCurrentBattle() {
        String name = "Test Character";

        CombatResponse resp = controller.addStoredCharacterToCurrentBattle(name);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void removeCombatantFromCurrentBattle() {
        String label = "Test Character";

        CombatResponse resp = controller.removeCombatantFromCurrentBattle(label);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void showBattle() {
        // Mock service.
        Battle battle = new Battle();
        battle.getLogs().add(new BattleLog("Test Log"));
        when(service.getBattle()).thenReturn(battle);

        ShowBattleResponse resp = controller.showBattle();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
        assertEquals(0, resp.getBattle().getCombatants().size());
        assertNull(resp.getBattle().getNextStep());
        assertEquals(1, resp.getBattle().getLogs().size());
    }
}