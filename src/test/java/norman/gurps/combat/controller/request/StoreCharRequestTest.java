package norman.gurps.combat.controller.request;

import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoreCharRequestTest {
    @Test
    void toGameChar() throws Exception {
        StoreCharRequest req = new StoreCharRequest();
        req.setName("Test Character Name");
        req.setStrength(14);
        req.setDexterity(13);
        req.setIntelligence(12);
        req.setHealth(11);

        GameChar gameChar = req.toGameChar();
        
        assertEquals(gameChar.getName(), "Test Character Name");
        assertEquals(gameChar.getStrength(), 14);
        assertEquals(gameChar.getDexterity(), 13);
        assertEquals(gameChar.getIntelligence(), 12);
        assertEquals(gameChar.getHealth(), 11);
    }
}