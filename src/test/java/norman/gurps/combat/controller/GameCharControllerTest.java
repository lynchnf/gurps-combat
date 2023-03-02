package norman.gurps.combat.controller;

import norman.gurps.combat.controller.response.BasicResponse;
import norman.gurps.combat.controller.response.GameCharsResponse;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.service.GameCharService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        BasicResponse resp = controller.storeChar(gameChar);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void removeChar() {
        String name = "Test Character";

        BasicResponse resp = controller.removeChar(name);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void showStoredChars() {
        List<GameChar> storedGameChars = new ArrayList<>();
        GameChar testGameChar = new GameChar();
        testGameChar.setName("Test Character");
        storedGameChars.add(testGameChar);
        when(service.getStoredGameChars()).thenReturn(storedGameChars);

        GameCharsResponse resp = controller.showStoredChars();

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
        assertEquals(1, resp.getGameChars().size());
    }
}