package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.LabelRequest;
import norman.gurps.combat.controller.request.NameRequest;
import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.controller.response.BattleResponse;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        BasicResponse resp = controller.createEmptyBattle();

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void deleteCurrentBattle() {
        BasicResponse resp = controller.deleteCurrentBattle();

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void addStoredCharacterToCurrentBattle() {
        NameRequest req = new NameRequest();
        req.setName("Test Character");

        BasicResponse resp = controller.addStoredCharacterToCurrentBattle(req);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void removeCombatantFromCurrentBattle() {
        LabelRequest req = new LabelRequest();
        req.setLabel("Test Character");

        BasicResponse resp = controller.removeCombatantFromCurrentBattle(req);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void showBattle() {
        // Mock service.
        Battle battle = new Battle();
        battle.getBattleLogs().add(new BattleLog("Test Log"));
        when(service.getBattle()).thenReturn(battle);

        BattleResponse resp = controller.showBattle();

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
        assertEquals(0, resp.getBattle().getCombatants().size());
        assertNull(resp.getBattle().getNextStep());
        assertEquals(1, resp.getBattle().getBattleLogs().size());
    }
}
