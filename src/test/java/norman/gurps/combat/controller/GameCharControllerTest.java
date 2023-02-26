package norman.gurps.combat.controller;

import norman.gurps.combat.controller.request.RemoveCharRequest;
import norman.gurps.combat.controller.request.StoreCharRequest;
import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowStoredCharsResponse;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.GameCharService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameCharControllerTest {
    GameCharController controller;
    GameCharService service;

    @BeforeEach
    void setUp() {
        // Mock service.
        service = mock(GameCharService.class);
        when(service.validate(any(GameChar.class))).thenReturn(new ArrayList<>());
        Map<String, GameChar> gameChars = new HashMap<>();
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character Name");
        gameChar.setStrength(14);
        gameChar.setDexterity(13);
        gameChar.setIntelligence(12);
        gameChar.setHealth(11);
        gameChars.put(gameChar.getName(), gameChar);
        when(service.getStoredGameChars()).thenReturn(gameChars);

        controller = new GameCharController(service);
    }

    @Test
    void storeCharHappyPath() throws Exception {
        StoreCharRequest req = new StoreCharRequest();
        req.setName("Another Test Character Name");
        req.setStrength(10);
        req.setDexterity(10);
        req.setIntelligence(10);
        req.setHealth(10);

        CombatResponse resp = controller.storeChar(req);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void storeCharAlreadyExists() throws Exception {
        StoreCharRequest req = new StoreCharRequest();
        req.setName("Test Character Name");
        req.setStrength(10);
        req.setDexterity(10);
        req.setIntelligence(10);
        req.setHealth(10);

        CombatResponse resp = controller.storeChar(req);

        assertFalse(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void removeCharHappyPath() throws Exception {
        RemoveCharRequest req = new RemoveCharRequest();
        req.setName("Test Character Name");

        CombatResponse resp = controller.removeChar(req);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void removeCharNotExist() throws Exception {
        RemoveCharRequest req = new RemoveCharRequest();
        req.setName("Another Test Character Name");

        CombatResponse resp = controller.removeChar(req);

        assertFalse(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void showStoredChars() {
        ShowStoredCharsResponse resp = controller.showStoredChars();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
        assertEquals(1, resp.getGameChars().size());
        GameChar gameChar = resp.getGameChars().get(0);
        assertEquals("Test Character Name", gameChar.getName());
        assertEquals(14, gameChar.getStrength());
        assertEquals(13, gameChar.getDexterity());
        assertEquals(12, gameChar.getIntelligence());
        assertEquals(11, gameChar.getHealth());
    }
}