package norman.gurps.combat.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.config.GurpsCombatConfig;
import norman.gurps.combat.controller.BattleController;
import norman.gurps.combat.service.BattleService;
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
@ContextConfiguration(
        classes = {BattleController.class, BattleService.class, GameCharService.class, GurpsCombatConfig.class})
@WebMvcTest
class BattleControllerIT {
    @Value("${storage.dir.name}")
    String storageDirName;
    @Value("${storage.battle.file.name}")
    String storageBattleFileName;
    @Value("${storage.game.char.file.name}")
    String storageGameCharFileName;
    File storageBattleFile;
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
        storageBattleFile = new File(storageDir, storageBattleFileName);
        if (storageBattleFile.exists()) {
            if (!storageBattleFile.delete()) {
                throw new RuntimeException("SETUP: Unable to delete file " + storageBattleFile + ".");
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
        if (storageBattleFile.exists()) {
            if (!storageBattleFile.delete()) {
                throw new RuntimeException("SETUP: Unable to delete file " + storageBattleFile + ".");
            }
        }
        if (storageGameCharFile.exists()) {
            if (!storageGameCharFile.delete()) {
                throw new RuntimeException("SETUP: Unable to delete file " + storageGameCharFile + ".");
            }
        }
    }

    @Test
    void createEmptyBattle() throws Exception {
        //@formatter:off
        MvcResult result = mockMvc.perform(post("/battle/create")
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

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(0, battleJsonNode.get("combatants").size());
        assertTrue(battleJsonNode.get("nextStep").isNull());
        assertTrue(battleJsonNode.get("logs").isArray());
        assertEquals(1, battleJsonNode.get("logs").size());
    }

    @Test
    void deleteCurrentBattle() throws Exception {
        // Preload file with data.
        String resourceName = "integration/battle-controller/battle-delete/battle.json";
        String battleJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/battle/delete")
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

        assertFalse(storageBattleFile.exists());
    }

    @Test
    void addStoredCharacterToCurrentBattle_empty_battle() throws Exception {
        // Create game character in storage
        String resourceName = "integration/battle-controller/battle-add-char/game-char.json";
        String gameCharJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        // Create empty battle in storage.
        String resourceName2 = "integration/battle-controller/battle-add-char/battle_empty_battle.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(storageBattleFile));
        writer2.write(battleJson);
        writer2.close();

        //@formatter:off
        String requestData = "Test Character";
        MvcResult result = mockMvc.perform(post("/battle/add/char")
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

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(1, battleJsonNode.get("combatants").size());
        assertEquals("Test Character", battleJsonNode.get("combatants").get(0).get("label").textValue());
        assertEquals("Test Character", battleJsonNode.get("combatants").get(0).get("gameChar").get("name").textValue());
        assertTrue(battleJsonNode.get("nextStep").isNull());
        assertTrue(battleJsonNode.get("logs").isArray());
        assertEquals(2, battleJsonNode.get("logs").size());
    }

    @Test
    void addStoredCharacterToCurrentBattle_char_already_in_battle() throws Exception {
        // Create game character in storage
        String resourceName = "integration/battle-controller/battle-add-char/game-char.json";
        String gameCharJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        // Create empty battle in storage.
        String resourceName2 = "integration/battle-controller/battle-add-char/battle_char_already_in_battle.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(storageBattleFile));
        writer2.write(battleJson);
        writer2.close();

        //@formatter:off
        String requestData = "Test Character";
        MvcResult result = mockMvc.perform(post("/battle/add/char")
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

        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals("Test Character 2", battleJsonNode.get("combatants").get(1).get("label").textValue());
        assertEquals("Test Character", battleJsonNode.get("combatants").get(1).get("gameChar").get("name").textValue());
        assertTrue(battleJsonNode.get("nextStep").isNull());
        assertTrue(battleJsonNode.get("logs").isArray());
        assertEquals(3, battleJsonNode.get("logs").size());
    }

    @Test
    void removeCombatantFromCurrentBattle() throws Exception {
        // Create battle with combatant in storage.
        String resourceName = "integration/battle-controller/battle-remove-char/battle.json";
        String battleJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        String requestData = "Test Character";
        MvcResult result = mockMvc.perform(post("/battle/remove/char")
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

        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(0, battleJsonNode.get("combatants").size());
        assertTrue(battleJsonNode.get("nextStep").isNull());
        assertTrue(battleJsonNode.get("logs").isArray());
        assertEquals(3, battleJsonNode.get("logs").size());
    }

    @Test
    void showBattle() throws Exception {
        // Preload storage with a battle.
        String resourceName = "integration/battle-controller/battle-show/battle.json";
        String battleJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(get("/battle/show")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        System.out.println("result=" + result);
        System.out.println("response=" + result.getResponse());
        System.out.println("content=" + result.getResponse().getContentAsString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(1, jsonNode.get("messages").size());
        assertTrue(jsonNode.get("messages").get(0).isTextual());
        assertTrue(jsonNode.get("battle").get("combatants").isArray());
        assertTrue(jsonNode.get("battle").get("logs").isArray());
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