package norman.gurps.combat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GurpsCombatConfigTest {
    GurpsCombatConfig config;

    @BeforeEach
    void setUp() {
        config = new GurpsCombatConfig();
    }

    @Test
    void objectMapper() {
        ObjectMapper mapper = config.objectMapper();

        assertNotNull(mapper);
    }
}