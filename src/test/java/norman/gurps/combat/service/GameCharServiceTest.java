package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.GameChar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameCharServiceTest {
    GameCharService service;
    File tempDir;
    File tempFile;
    GameChar gameChar;

    @BeforeEach
    void setUp() throws Exception {
        service = new GameCharService(new ObjectMapper());

        // Override storage directory for testing.
        tempDir = Files.createTempDirectory("gurps-combat-temp-").toFile();
        ReflectionTestUtils.setField(service, "storageDir", tempDir);

        // Override storage file for testing.
        tempFile = new File(tempDir, "game-char.json");
        ReflectionTestUtils.setField(service, "storageGameCharFile", tempFile);

        gameChar = TestHelper.getGameChar1();
    }

    @Test
    void validate1() {
        List<String> errors = service.validate(gameChar);

        assertEquals(0, errors.size());
    }

    @Test
    void validate2() {
        GameChar gameChar2 = TestHelper.getGameChar2();

        List<String> errors = service.validate(gameChar2);

        assertEquals(0, errors.size());
    }

    @Test
    void storeChar() throws Exception {
        service.storeChar(gameChar);

        // Validate game character was written to local storage.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        GameChar[] gameChars = mapper.readValue(file, GameChar[].class);
        assertEquals(1, gameChars.length);
        assertEquals("Bob the Example", gameChars[0].getName());
    }

    @Test
    void removeChar() throws Exception {
        // Preload local storage.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        List<GameChar> gameCharList = new ArrayList<>();
        gameCharList.add(gameChar);
        mapper.writeValue(file, gameCharList);

        service.removeChar("Bob the Example");

        // Validate game character was removed from to local storage.
        GameChar[] gameChars = mapper.readValue(file, GameChar[].class);
        assertEquals(0, gameChars.length);
    }

    @Test
    void getStoredGameChars() throws Exception {
        // Preload local storage.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        List<GameChar> gameCharList = new ArrayList<>();
        gameCharList.add(gameChar);
        mapper.writeValue(file, gameCharList);

        List<GameChar> gameChars = service.getStoredGameChars();

        assertEquals(1, gameChars.size());
        assertEquals("Bob the Example", gameChars.get(0).getName());
    }
}
