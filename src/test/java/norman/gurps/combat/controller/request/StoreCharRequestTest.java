package norman.gurps.combat.controller.request;

import norman.gurps.combat.JsonTestHelper;
import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreCharRequestTest extends JsonTestHelper {
    @BeforeEach
    void setUp() {
        initialize();
    }

    @Test
    void happyPath() throws Exception {
        doTheTest(StoreCharRequest.class, "store_char_request.json");
    }

    @Test
    void toGameChar() throws Exception {
        StoreCharRequest req = getObject(StoreCharRequest.class, "store_char_request.json");
        GameChar gameChar = req.toGameChar();
        assertEquals(gameChar.getName(), "Test Character Name");
        assertEquals(gameChar.getStrength(), 14);
        assertEquals(gameChar.getDexterity(), 13);
        assertEquals(gameChar.getIntelligence(), 12);
        assertEquals(gameChar.getHealth(), 11);
    }
}