package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.RemoveCharRequest;
import norman.gurps.combat.controller.request.StoreCharRequest;
import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowStoredCharsResponse;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.GameCharService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GameCharController {
    private static Logger LOGGER = LoggerFactory.getLogger(GameCharController.class);
    private GameCharService service;

    @Autowired
    public GameCharController(GameCharService service) {
        this.service = service;
    }

    @PostMapping("/char/store")
    public CombatResponse storeChar(StoreCharRequest req) {
        CombatResponse resp = new CombatResponse();

        // Validate new Game Character.
        GameChar newGameChar = req.toGameChar();
        List<String> errors = service.validate(newGameChar);
        if (!errors.isEmpty()) {
            resp.setSuccessful(false);
            resp.setMessage(errors.get(0));
            return resp;
        }

        // Verify new Game Character does not already exist.
        Map<String, GameChar> gameChars = service.getStoredGameChars();
        if (gameChars.containsKey(newGameChar.getName())) {
            resp.setSuccessful(false);
            resp.setMessage(String.format("A character with name %s already exists in the stored characters.",
                    newGameChar.getName()));
            return resp;
        }

        // Save new Game Character.
        gameChars.put(newGameChar.getName(), newGameChar);
        service.saveStoredGameChars(gameChars);
        resp.setSuccessful(true);
        resp.setMessage(String.format("Successfully saved character %s to local storage.", newGameChar.getName()));
        return resp;
    }

    @PostMapping("/char/remove")
    public CombatResponse removeChar(RemoveCharRequest req) {
        CombatResponse resp = new CombatResponse();

        // Get name to remove.
        String name = StringUtils.trimToNull(req.getName());
        if (name == null) {
            resp.setSuccessful(false);
            resp.setMessage("Name to remove may not be blank.");
            return resp;
        }

        // Verify character to remove exists.
        Map<String, GameChar> gameChars = service.getStoredGameChars();
        if (!gameChars.containsKey(name)) {
            resp.setSuccessful(false);
            resp.setMessage(String.format("Character %s not found in local storage.", name));
            return resp;
        }

        // Remove character.
        gameChars.remove(name);
        service.saveStoredGameChars(gameChars);
        resp.setSuccessful(true);
        resp.setMessage(String.format("Successfully removed character %s from local storage.", name));
        return resp;
    }

    @GetMapping("/char/show")
    public ShowStoredCharsResponse showStoredChars() {
        ShowStoredCharsResponse resp = new ShowStoredCharsResponse();
        resp.setSuccessful(true);
        Map<String, GameChar> gameChars = service.getStoredGameChars();
        if (gameChars.isEmpty()) {
            resp.setMessage("No characters found in local storage.");
        } else {
            resp.setMessage(String.format("Found %d characters in local storage.", gameChars.size()));
            resp.getGameChars().addAll(gameChars.values());
        }
        return resp;
    }
}
