package norman.gurps.combat.controller.response;

import norman.gurps.combat.JsonTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CombatResponseTest extends JsonTestHelper {
    @BeforeEach
    void setUp() {
        initialize();
    }

    @Test
    void failure() throws Exception {
        doTheTest(CombatResponse.class, "combat_response_failure.json");
    }

    @Test
    void success() throws Exception {
        doTheTest(CombatResponse.class, "combat_response_failure.json");
    }
}