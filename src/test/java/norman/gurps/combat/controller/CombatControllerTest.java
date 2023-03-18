package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.NextStepRequest;
import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.Phase;
import norman.gurps.combat.service.CombatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CombatControllerTest {
    CombatController controller;
    CombatService service;

    @BeforeEach
    void setUp() {
        service = mock(CombatService.class);
        controller = new CombatController(service);
    }

    @Test
    void startCombat() {
        BasicResponse resp = controller.startCombat();

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void nextStepInCombat() {
        // Mock service.
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_TARGET_AND_WEAPON);
        nextStep1.setInputNeeded(false);
        nextStep1.setMessage("Message from RESOLVE_ACTION");
        when(service.nextStep(eq(Phase.RESOLVE_ACTION), eq(Action.ATTACK), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(nextStep1);

        NextStep nextStep2 = new NextStep();
        nextStep2.setRound(1);
        nextStep2.setIndex(0);
        nextStep2.setPhase(Phase.RESOLVE_TARGET_AND_WEAPON);
        nextStep2.setInputNeeded(true);
        nextStep2.setMessage("Message from PROMPT_FOR_TARGET_AND_WEAPON");
        when(service.nextStep(eq(Phase.PROMPT_FOR_TARGET_AND_WEAPON), any(Action.class), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(nextStep2);

        NextStepRequest req = new NextStepRequest();
        req.setPhase(Phase.RESOLVE_ACTION);
        req.setAction(Action.ATTACK);

        BasicResponse resp = controller.nextStepInCombat(req);

        assertTrue(resp.getSuccessful());
        assertEquals(2, resp.getMessages().size());
        assertEquals("Message from RESOLVE_ACTION", resp.getMessages().get(0));
        assertEquals("Message from PROMPT_FOR_TARGET_AND_WEAPON", resp.getMessages().get(1));
    }
}