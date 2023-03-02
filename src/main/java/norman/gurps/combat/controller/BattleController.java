package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.controller.response.BattleResponse;
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
    public BasicResponse createEmptyBattle() {
        LOGGER.debug("Creating empty battle");
        BasicResponse resp = new BasicResponse();

        try {
            service.createBattle();
            resp.setSuccessful(true);
            resp.getMessages().add("Successfully created a new empty battle.");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @PostMapping("/battle/delete")
    public BasicResponse deleteCurrentBattle() {
        LOGGER.debug("Deleting current battle");
        BasicResponse resp = new BasicResponse();

        try {
            service.deleteBattle();
            resp.setSuccessful(true);
            resp.getMessages().add("Successfully deleted the current battle.");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @PostMapping("/battle/add/char")
    public BasicResponse addStoredCharacterToCurrentBattle(@RequestBody String name) {
        LOGGER.debug("Add game char to current battle: {}", name);
        BasicResponse resp = new BasicResponse();

        try {
            String label = service.addCharToBattle(name);
            resp.setSuccessful(true);
            resp.getMessages()
                    .add("Successfully added character " + name + " to current battle with label " + label + ".");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @PostMapping("/battle/remove/char")
    public BasicResponse removeCombatantFromCurrentBattle(@RequestBody String label) {
        LOGGER.debug("Removing combatant from current battle: {}", label);
        BasicResponse resp = new BasicResponse();

        try {
            service.removeCharFromBattle(label);
            resp.setSuccessful(true);
            resp.getMessages().add("Successfully removed combatant " + label + " from current battle.");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @GetMapping("/battle/show")
    public BattleResponse showBattle() {
        LOGGER.debug("Showing everything from current battle");
        BattleResponse resp = new BattleResponse();

        try {
            Battle battle = service.getBattle();
            resp.setSuccessful(true);
            resp.getMessages()
                    .add("Found a battle with " + battle.getCombatants().size() + " combatants in local storage.");
            resp.setBattle(battle);
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }
}