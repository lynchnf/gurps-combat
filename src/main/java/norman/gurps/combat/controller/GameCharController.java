package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowStoredCharsResponse;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.GameCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameCharController {
    private static Logger LOGGER = LoggerFactory.getLogger(GameCharController.class);
    private GameCharService service;

    public GameCharController(GameCharService service) {
        this.service = service;
    }

    @PostMapping("/char/store")
    public CombatResponse storeChar(@RequestBody GameChar gameChar) {
        LOGGER.debug("Storing game character: {}", gameChar.getName());
        CombatResponse resp = new CombatResponse();

        // Validate new Game Character.
        List<String> errors = service.validate(gameChar);
        if (!errors.isEmpty()) {
            resp.setSuccessful(false);
            resp.setMessage(errors.get(0));
            return resp;
        }

        // Add new Game Character.
        try {
            service.storeChar(gameChar);
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Successfully saved Game Character " + gameChar.getName() + " to local storage.");
        return resp;
    }

    @PostMapping("/char/remove")
    public CombatResponse removeChar(@RequestBody String name) {
        LOGGER.debug("Removing stored game character: {}", name);
        CombatResponse resp = new CombatResponse();

        // Delete Game Character.
        try {
            service.removeChar(name);
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        resp.setMessage("Successfully removed character " + name + " from local storage.");
        return resp;
    }

    @GetMapping("/char/show")
    public ShowStoredCharsResponse showStoredChars() {
        LOGGER.debug("Showing all stored game characters");
        ShowStoredCharsResponse resp = new ShowStoredCharsResponse();

        List<GameChar> gameChars = null;
        try {
            gameChars = service.getStoredGameChars();
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.setMessage(e.getMessage());
            return resp;
        }
        resp.setSuccessful(true);
        if (gameChars == null) {
            resp.setMessage("No Game Characters found in local storage.");
        } else {
            resp.setMessage("Found " + gameChars.size() + " Game Characters in local storage.");
            resp.getGameChars().addAll(gameChars);
        }
        return resp;
    }
}
