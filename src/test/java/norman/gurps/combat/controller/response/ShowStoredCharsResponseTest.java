package norman.gurps.combat.controller.response;

import norman.gurps.combat.JsonTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShowStoredCharsResponseTest extends JsonTestHelper {
    @BeforeEach
    void setUp() {
        initialize();
    }

    @Test
    void happyPath() throws Exception {
        doTheTest(ShowStoredCharsResponse.class, "show_stored_chars_response.json");
    }
}