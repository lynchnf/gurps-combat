package norman.gurps.combat.controller.request;

import norman.gurps.combat.JsonTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveCharRequestTest extends JsonTestHelper {
    @BeforeEach
    void setUp() {
        initialize();
    }

    @Test
    void happyPath() throws Exception {
        doTheTest(RemoveCharRequest.class, "remove_char_request.json");
    }
}