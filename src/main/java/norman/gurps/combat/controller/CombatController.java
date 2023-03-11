package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.NextStepRequest;
import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.Defense;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.Phase;
import norman.gurps.combat.service.CombatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CombatController {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatController.class);
    private CombatService service;

    public CombatController(CombatService service) {
        this.service = service;
    }

    @PostMapping("/combat/start")
    public BasicResponse startCombat() {
        LOGGER.debug("Starting combat.");
        BasicResponse resp = new BasicResponse();

        try {
            service.startCombat();
            resp.setSuccessful(true);
            resp.getMessages().add("Successfully started Combat.");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @PostMapping("/combat/next")
    public BasicResponse nextStepInCombat(@RequestBody NextStepRequest req) {
        LOGGER.debug("Taking the next step in combat.");
        BasicResponse resp = new BasicResponse();

        Phase phase = req.getPhase();
        Action action = req.getAction();
        String targetLabel = req.getTargetLabel();
        String weaponName = req.getWeaponName();
        String modeName = req.getModeName();
        Integer rollToHit = req.getRollToHit();
        Defense defense = req.getDefense();
        String defendingItemName = req.getDefendingItemName();
        Integer rollToDefend = req.getRollToDefend();
        Integer rollForDamage = req.getRollForDamage();

        boolean inputNeeded;
        try {
            do {
                NextStep nextStep = service.nextStep(phase, action, targetLabel, weaponName, modeName, rollToHit,
                        defense, defendingItemName, rollToDefend, rollForDamage);
                if (nextStep.getMessage() != null) {
                    resp.getMessages().add(nextStep.getMessage());
                }
                phase = nextStep.getPhase();
                inputNeeded = nextStep.getInputNeeded();
            } while (!inputNeeded);
            resp.setSuccessful(true);
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }
}