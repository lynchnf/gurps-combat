package norman.gurps.combat.controller;

import norman.gurps.combat.JsonTestHelper;
import norman.gurps.combat.controller.request.RemoveCharRequest;
import norman.gurps.combat.controller.request.StoreCharRequest;
import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowStoredCharsResponse;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.GameCharService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GameCharControllerTest {
    GameCharController controller;
    GameCharService service;
    JsonTestHelper helper;

    @BeforeEach
    void setUp() {
        helper = new JsonTestHelper();
        helper.initialize();

        // Mock service.
        service = Mockito.mock(GameCharService.class);
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
        StoreCharRequest req = helper.getObject(StoreCharRequest.class, "store_char_request.json");
        req.setName("Another Test Character Name");
        CombatResponse resp = controller.storeChar(req);
        assertTrue(resp.getSuccessful());
        assertNotNull(StringUtils.trimToNull(resp.getMessage()));
    }

    @Test
    void storeCharAlreadyExists() throws Exception {
        StoreCharRequest req = helper.getObject(StoreCharRequest.class, "store_char_request.json");
        CombatResponse resp = controller.storeChar(req);
        assertFalse(resp.getSuccessful());
        assertNotNull(StringUtils.trimToNull(resp.getMessage()));
    }

    @Test
    void removeCharHappyPath() throws Exception {
        RemoveCharRequest req = helper.getObject(RemoveCharRequest.class, "remove_char_request.json");
        CombatResponse resp = controller.removeChar(req);
        assertTrue(resp.getSuccessful());
        assertNotNull(StringUtils.trimToNull(resp.getMessage()));
    }

    @Test
    void removeCharNotExist() throws Exception {
        RemoveCharRequest req = helper.getObject(RemoveCharRequest.class, "remove_char_request.json");
        req.setName("Another Test Character Name");
        CombatResponse resp = controller.removeChar(req);
        assertFalse(resp.getSuccessful());
        assertNotNull(StringUtils.trimToNull(resp.getMessage()));
    }

    @Test
    void showStoredChars() {
        ShowStoredCharsResponse resp = controller.showStoredChars();
        assertTrue(resp.getSuccessful());
        assertNotNull(StringUtils.trimToNull(resp.getMessage()));
        assertEquals(resp.getGameChars().size(), 1);
        GameChar gameChar = resp.getGameChars().get(0);
        assertEquals(gameChar.getName(), "Test Character Name");
        assertEquals(gameChar.getStrength(), 14);
        assertEquals(gameChar.getDexterity(), 13);
        assertEquals(gameChar.getIntelligence(), 12);
        assertEquals(gameChar.getHealth(), 11);
    }
}