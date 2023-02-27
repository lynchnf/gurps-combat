package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.AddStoredCharacterToCurrentBattleRequest;
import norman.gurps.combat.controller.request.RemoveCombatantFromCurrentBattle;
import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowBattleResponse;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.BattleService;
import norman.gurps.combat.service.GameCharService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BattleController {
    private static Logger LOGGER = LoggerFactory.getLogger(BattleController.class);
    private BattleService service;
    private GameCharService gameCharService;

    public BattleController(BattleService service, GameCharService gameCharService) {
        this.service = service;
        this.gameCharService = gameCharService;
    }

    @PostMapping("/battle/create")
    public CombatResponse createEmptyBattle() {
        LOGGER.debug("Creating empty battle");
        CombatResponse resp = new CombatResponse();

        // Verify battle does not already exist.
        Battle battle = service.getBattle();
        if (battle != null) {
            resp.setSuccessful(false);
            resp.setMessage("A battle already exists in local storage.");
            return resp;
        }

        // Create battle.
        service.createBattle();
        resp.setSuccessful(true);
        resp.setMessage("Successfully created a new empty battle.");
        return resp;
    }

    @PostMapping("/battle/delete")
    public CombatResponse deleteCurrentBattle() {
        LOGGER.debug("Deleting current battle");
        CombatResponse resp = new CombatResponse();

        // Verify battle already exists.
        Battle battle = service.getBattle();
        if (battle == null) {
            resp.setSuccessful(false);
            resp.setMessage("A battle does not currently exist in local storage.");
            return resp;
        }

        // Delete battle.
        service.deleteBattle();
        resp.setSuccessful(true);
        resp.setMessage("Successfully deleted the current battle.");
        return resp;
    }

    @PostMapping("/battle/add/char")
    public CombatResponse addStoredCharacterToCurrentBattle(@RequestBody AddStoredCharacterToCurrentBattleRequest req) {
        LOGGER.debug("Add game char to current battle: {}", req);
        CombatResponse resp = new CombatResponse();

        // Get name to add.
        String name = StringUtils.trimToNull(req.getName());
        if (name == null) {
            resp.setSuccessful(false);
            resp.setMessage("Name to add to battle may not be blank.");
            return resp;
        }

        // Verify character to add exists.
        Map<String, GameChar> gameChars = gameCharService.getStoredGameChars();
        if (!gameChars.containsKey(name)) {
            resp.setSuccessful(false);
            resp.setMessage("Character " + name + " not found in local storage.");
            return resp;
        }

        // Verify battle already exists.
        Battle battle = service.getBattle();
        if (battle == null) {
            resp.setSuccessful(false);
            resp.setMessage("A battle does not currently exist in local storage.");
            return resp;
        }

        // Add char to battle.
        String label = service.addGameCharToBattle(gameChars.get(name));
        resp.setSuccessful(true);
        resp.setMessage("Successfully added character " + name + " to current battle with label " + label + ".");
        return resp;
    }

    @PostMapping("/battle/remove/char")
    public CombatResponse removeCombatantFromCurrentBattle(@RequestBody RemoveCombatantFromCurrentBattle req) {
        LOGGER.debug("Removing combatant from current battle: {}", req);
        CombatResponse resp = new CombatResponse();

        // Get label to remove.
        String label = StringUtils.trimToNull(req.getLabel());
        if (label == null) {
            resp.setSuccessful(false);
            resp.setMessage("Label to remove from battle may not be blank.");
            return resp;
        }

        // Verify battle already exists.
        Battle battle = service.getBattle();
        if (battle == null) {
            resp.setSuccessful(false);
            resp.setMessage("A battle does not currently exist in local storage.");
            return resp;
        }

        // Verify combatant exists in battle.
        if (!battle.getCombatants().containsKey(label)) {
            resp.setSuccessful(false);
            resp.setMessage("Combatant " + label + " not found in current battle.");
            return resp;
        }

        // Remove combatant from battle.
        service.removeCombatantFromBattle(label);
        resp.setSuccessful(true);
        resp.setMessage("Successfully removed combatant " + label + " from current battle.");
        return resp;
    }

    @GetMapping("/battle/show")
    public ShowBattleResponse showBattle() {
        LOGGER.debug("Showing everything from current battle");
        ShowBattleResponse resp = new ShowBattleResponse();

        // Verify battle already exists.
        Battle battle = service.getBattle();
        if (battle == null) {
            resp.setSuccessful(false);
            resp.setMessage("A battle does not currently exist in local storage.");
            return resp;
        }

        // Show the battle.
        resp.setSuccessful(true);
        resp.setMessage("Found a battle with " + battle.getCombatants().size() + " combatants in local storage.");
        resp.setBattle(battle);
        return resp;
    }
}
