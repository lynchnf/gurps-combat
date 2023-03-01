package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowBattleResponse;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BattleController {
    private static Logger LOGGER = LoggerFactory.getLogger(BattleController.class);
    private BattleService service;

    public BattleController(BattleService service) {
        this.service = service;
    }

    @PostMapping("/battle/create")
    public CombatResponse createEmptyBattle() {
        LOGGER.debug("Creating empty battle");
        CombatResponse resp = new CombatResponse();

        try {
            service.createBattle();
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Successfully created a new empty battle.");
        return resp;
    }

    @PostMapping("/battle/delete")
    public CombatResponse deleteCurrentBattle() {
        LOGGER.debug("Deleting current battle");
        CombatResponse resp = new CombatResponse();

        try {
            service.deleteBattle();
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Successfully deleted the current battle.");
        return resp;
    }

    @PostMapping("/battle/add/char")
    public CombatResponse addStoredCharacterToCurrentBattle(@RequestBody String name) {
        LOGGER.debug("Add game char to current battle: {}", name);
        CombatResponse resp = new CombatResponse();

        String label;
        try {
            label = service.addCharToBattle(name);
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Successfully added character " + name + " to current battle with label " + label + ".");
        return resp;
    }

    @PostMapping("/battle/remove/char")
    public CombatResponse removeCombatantFromCurrentBattle(@RequestBody String label) {
        LOGGER.debug("Removing combatant from current battle: {}", label);
        CombatResponse resp = new CombatResponse();

        try {
            service.removeCharFromBattle(label);
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Successfully removed combatant " + label + " from current battle.");
        return resp;
    }

    @GetMapping("/battle/show")
    public ShowBattleResponse showBattle() {
        LOGGER.debug("Showing everything from current battle");
        ShowBattleResponse resp = new ShowBattleResponse();

        Battle battle = null;
        try {
            battle = service.getBattle();
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Found a battle with " + battle.getCombatants().size() + " combatants in local storage.");
        resp.setBattle(battle);
        return resp;
    }
}