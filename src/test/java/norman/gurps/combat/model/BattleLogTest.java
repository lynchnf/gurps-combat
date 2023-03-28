package norman.gurps.combat.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BattleLogTest {
    @Test
    void constructor() {
        BattleLog log = new BattleLog("Test message.");

        long currentTimeMillis = System.currentTimeMillis();
        assertEquals(currentTimeMillis, log.getTimeMillis(), 10);
    }
}
