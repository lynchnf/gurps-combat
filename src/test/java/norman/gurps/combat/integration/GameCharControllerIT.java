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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
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
        //@formatter:off
        String requestData = "{\"name\": \"Test Character Name\"," +
                " \"strength\": 14," +
                " \"dexterity\": 13," +
                " \"intelligence\": 12," +
                " \"health\": 11}";

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
        assertTrue(jsonNode.get("message").isTextual());

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
        assertTrue(jsonNode2.get("message").isTextual());
    }

    @Test
    void removeChar() throws Exception {
        // Preload file with data.
        //@formatter:off
        String gameCharJson = "[{\"name\": \"Test Character Name\"," +
                " \"strength\": 14," +
                " \"dexterity\": 13," +
                " \"intelligence\": 12," +
                " \"health\": 11}]";
        //@formatter:on
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        //@formatter:off
        String requestData = "{\"name\": \"Test Character Name\"}";

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
        assertTrue(jsonNode.get("message").isTextual());

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
        assertTrue(jsonNode2.get("message").isTextual());
    }

    @Test
    void showStoredChars() throws Exception {
        // Preload file with data.
        //@formatter:off
        String gameCharJson = "[{\"name\": \"Test Character Name\"," +
                " \"strength\": 14," +
                " \"dexterity\": 13," +
                " \"intelligence\": 12," +
                " \"health\": 11}," +
                " {\"name\": \"Another Test Character Name\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}]";
        //@formatter:on
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
        assertTrue(jsonNode.get("message").isTextual());
        assertTrue(jsonNode.get("gameChars").isArray());
        assertEquals(2, jsonNode.get("gameChars").size());
    }
}