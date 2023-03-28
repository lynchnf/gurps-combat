package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.NextStepRequest;
import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.service.CombatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

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
        NextStep nextStep = new NextStep();
        nextStep.setRound(1);
        nextStep.setIndex(0);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_ACTION);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("1/0 : Bob the Example, please chose an action.");
        Mockito.when(
                service.nextStep(CombatPhase.PROMPT_FOR_ACTION, null, null, null, null, null, null, null, null, null,
                        null, null, null)).thenReturn(nextStep);

        NextStepRequest req = new NextStepRequest();
        req.setCombatPhase(CombatPhase.PROMPT_FOR_ACTION);

        BasicResponse resp = controller.nextStepInCombat(req);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }
}
