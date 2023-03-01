package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.CombatResponse;
import norman.gurps.combat.controller.response.ShowStoredCharsResponse;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.GameCharService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameCharControllerTest {
    GameCharController controller;
    GameCharService service;

    @BeforeEach
    void setUp() {
        // Mock service.
        service = mock(GameCharService.class);
        controller = new GameCharController(service);
    }

    @Test
    void storeChar() {
        GameChar gameChar = new GameChar();
        gameChar.setName("Test Character");

        CombatResponse resp = controller.storeChar(gameChar);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void removeChar() {
        String name = "Test Character";

        CombatResponse resp = controller.removeChar(name);

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
    }

    @Test
    void showStoredChars() {
        List<GameChar> storedGameChars = new ArrayList<>();
        GameChar testGameChar = new GameChar();
        testGameChar.setName("Test Character");
        storedGameChars.add(testGameChar);
        when(service.getStoredGameChars()).thenReturn(storedGameChars);

        ShowStoredCharsResponse resp = controller.showStoredChars();

        assertTrue(resp.getSuccessful());
        assertNotNull(resp.getMessage());
        assertEquals(1, resp.getGameChars().size());
    }
}