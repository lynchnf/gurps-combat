package norman.gurps.combat.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.config.GurpsCombatConfig;
import norman.gurps.combat.controller.GameCharController;
import norman.gurps.combat.service.GameCharService;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {GameCharController.class, GameCharService.class, GurpsCombatConfig.class})
@WebMvcTest
class GameCharControllerIT {
    @Value("${storage.dir.name}")
    String storageDirName;
    @Value("${storage.game.char.file.name}")
    String storageGameCharFileName;
    File storageGameCharFile;
    ClassLoader loader;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        loader = Thread.currentThread().getContextClassLoader();

        File storageDir = new File(SystemUtils.USER_HOME, storageDirName);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw new RuntimeException("SETUP: Unable to create directory " + storageDir + ".");
            }
        }
        storageGameCharFile = new File(storageDir, storageGameCharFileName);
        if (storageGameCharFile.exists()) {
            if (!storageGameCharFile.delete()) {
                throw new RuntimeException("SETUP: Unable to delete file " + storageGameCharFile + ".");
            }
        }
    }

    @AfterEach
    void tearDown() {
        if (storageGameCharFile.exists()) {
            if (!storageGameCharFile.delete()) {
                throw new RuntimeException("TEARDOWN: Unable to delete file " + storageGameCharFile + ".");
            }
        }
    }

    @Test
    void storeChar() throws Exception {
        String resourceName = "integration/game-char-controller/char-store/request.json";
        String requestData = readResource(resourceName);

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/char/store")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(1, jsonNode.get("messages").size());
        assertTrue(jsonNode.get("messages").get(0).isTextual());

        // Do it again.
        //@formatter:off
        MvcResult result2 = mockMvc.perform(post("/char/store")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        JsonNode jsonNode2 = mapper.readTree(result2.getResponse().getContentAsString());
        assertTrue(jsonNode2.get("successful").isBoolean());
        // This time, it should fail.
        assertFalse(jsonNode2.get("successful").asBoolean());
        assertTrue(jsonNode2.get("messages").isArray());
        assertEquals(1, jsonNode2.get("messages").size());
        assertTrue(jsonNode2.get("messages").get(0).isTextual());
    }

    @Test
    void removeChar() throws Exception {
        // Preload file with data.
        String resourceName = "integration/game-char-controller/char-remove/game-char.json";
        String gameCharJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        String requestData = "Test Character";

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/char/remove")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(1, jsonNode.get("messages").size());
        assertTrue(jsonNode.get("messages").get(0).isTextual());

        // Do it again.
        //@formatter:off
        MvcResult result2 = mockMvc.perform(post("/char/remove")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        JsonNode jsonNode2 = mapper.readTree(result2.getResponse().getContentAsString());
        assertTrue(jsonNode2.get("successful").isBoolean());
        // This time, it should fail.
        assertFalse(jsonNode2.get("successful").asBoolean());
        assertTrue(jsonNode2.get("messages").isArray());
        assertEquals(1, jsonNode2.get("messages").size());
        assertTrue(jsonNode2.get("messages").get(0).isTextual());
    }

    @Test
    void showStoredChars() throws Exception {
        // Preload file with data.
        String resourceName = "integration/game-char-controller/char-show/game-char.json";
        String gameCharJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(get("/char/show")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(1, jsonNode.get("messages").size());
        assertTrue(jsonNode.get("messages").get(0).isTextual());
        assertTrue(jsonNode.get("gameChars").isArray());
        assertEquals(1, jsonNode.get("gameChars").size());
        assertEquals("Test Character", jsonNode.get("gameChars").get(0).get("name").textValue());
    }

    private String readResource(String resourceName) throws IOException {
        InputStream stream = loader.getResourceAsStream(resourceName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = null;
        String line;
        while ((line = reader.readLine()) != null) {
            if (sb == null) {
                sb = new StringBuilder(line);
            } else {
                sb.append(System.lineSeparator()).append(line);
            }
        }
        reader.close();
        return sb.toString();
    }
}