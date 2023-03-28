package norman.gurps.combat.controller;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.controller.request.NameRequest;
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
    GameChar gameChar;

    @BeforeEach
    void setUp() {
        // Mock service.
        service = mock(GameCharService.class);
        controller = new GameCharController(service);

        gameChar = TestHelper.getGameChar1();
    }

    @Test
    void storeChar() {
        BasicResponse resp = controller.storeChar(gameChar);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void removeChar() {
        NameRequest req = new NameRequest();
        req.setName("Bob the Example");

        BasicResponse resp = controller.removeChar(req);

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
    }

    @Test
    void showStoredChars() {
        List<GameChar> storedGameChars = new ArrayList<>();
        storedGameChars.add(gameChar);
        GameChar gameChar2 = TestHelper.getGameChar2();
        storedGameChars.add(gameChar2);
        when(service.getStoredGameChars()).thenReturn(storedGameChars);

        GameCharsResponse resp = controller.showStoredChars();

        assertTrue(resp.getSuccessful());
        assertEquals(1, resp.getMessages().size());
        assertEquals(2, resp.getGameChars().size());
        assertEquals("Bob the Example", resp.getGameChars().get(0).getName());
        assertEquals("Grunt", resp.getGameChars().get(1).getName());
    }
}
