package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.controller.response.GameCharsResponse;
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
    public BasicResponse storeChar(@RequestBody GameChar gameChar) {
        LOGGER.debug("Storing game character {}.", gameChar.getName());
        BasicResponse resp = new BasicResponse();

        // Validate new Game Character.
        List<String> errors = service.validate(gameChar);
        if (!errors.isEmpty()) {
            resp.setSuccessful(false);
            resp.getMessages().addAll(errors);
            return resp;
        }

        // Add new Game Character.
        try {
            service.storeChar(gameChar);
            resp.setSuccessful(true);
            resp.getMessages().add("Successfully saved Game Character " + gameChar.getName() + " to local storage.");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @PostMapping("/char/remove")
    public BasicResponse removeChar(@RequestBody String name) {
        LOGGER.debug("Removing stored game character {}.", name);
        BasicResponse resp = new BasicResponse();

        // Delete Game Character.
        try {
            service.removeChar(name);
            resp.setSuccessful(true);
            resp.getMessages().add("Successfully removed character " + name + " from local storage.");
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }

    @GetMapping("/char/show")
    public GameCharsResponse showStoredChars() {
        LOGGER.debug("Showing all stored game characters.");
        GameCharsResponse resp = new GameCharsResponse();

        try {
            List<GameChar> gameChars = service.getStoredGameChars();
            resp.setSuccessful(true);
            if (gameChars == null) {
                resp.getMessages().add("No Game Characters found in local storage.");
            } else {
                resp.getMessages().add("Found " + gameChars.size() + " Game Characters in local storage.");
                resp.getGameChars().addAll(gameChars);
            }
            return resp;
        } catch (LoggingException e) {
            resp.setSuccessful(false);
            resp.getMessages().add(e.getMessage());
            return resp;
        }
    }
}
