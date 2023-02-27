package norman.gurps.combat.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.config.GurpsCombatConfig;
import norman.gurps.combat.controller.BattleController;
import norman.gurps.combat.model.Battle;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    @Value("${storage.game.char.file.name}")
    String storageGameCharFileName;
    @Value("${storage.battle.file.name}")
    String storageBattleFileName;
    File storageGameCharFile;
    File storageBattleFile;
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
        storageBattleFile = new File(storageDir, storageBattleFileName);
        if (storageBattleFile.exists()) {
            if (!storageBattleFile.delete()) {
                throw new RuntimeException("SETUP: Unable to delete file " + storageBattleFile + ".");
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
        if (storageBattleFile.exists()) {
            if (!storageBattleFile.delete()) {
                throw new RuntimeException("TEARDOWN: Unable to delete file " + storageBattleFile + ".");
            }
        }
    }

    @Test
    void createEmptyBattle() throws Exception {
        //@formatter:off
        MvcResult result = mockMvc.perform(post("/battle/create")
                        .contentType(MediaType.APPLICATION_JSON) )
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("message").isTextual());

        assertTrue(storageBattleFile.exists());
    }

    @Test
    void deleteCurrentBattle() throws Exception {
        // Preload file with data.
        //@formatter:off
        String battleJson = "{\"combatants\": {\"Test Character\": {\"label\": \"Test Character\"," +
                " \"name\": \"Test Character\"," +
                " \"strength\": 14," +
                " \"dexterity\": 13," +
                " \"intelligence\": 12," +
                " \"health\": 11}," +
                " \"Another Character\": {\"label\": \"Another Character\"," +
                " \"name\": \"Another Character\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}," +
                " \"Another Character 2\": {\"label\": \"Another Character 2\"," +
                " \"name\": \"Another Character\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}}," +
                " \"nextStep\": null," +
                " \"logs\": [{\"timeMillis\": 1677430948958," +
                " \"message\": \"Battle created.\"}," +
                " {\"timeMillis\": 1677430966402," +
                " \"message\": \"Combatant Test Character added to battle.\"}," +
                " {\"timeMillis\": 1677430987182," +
                " \"message\": \"Combatant Another Character added to battle.\"}," +
                " {\"timeMillis\": 1677430989148," +
                " \"message\": \"Combatant Another Character 2 added to battle.\"}]}";
        //@formatter:on
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
        assertTrue(jsonNode.get("message").isTextual());

        assertFalse(storageBattleFile.exists());
    }

    @Test
    void addStoredCharacterToCurrentBattle() throws Exception {
        // Preload file with data.
        //@formatter:off
        String gameCharJson = "[{\"name\": \"Test Character\", " +
                " \"strength\": 14, " +
                " \"dexterity\": 13, " +
                " \"intelligence\": 12, " +
                " \"health\": 11}, " +
                " {\"name\": \"Another Character\", " +
                " \"strength\": 10, " +
                " \"dexterity\": 10, " +
                " \"intelligence\": 10, " +
                " \"health\": 10}]";
        //@formatter:on
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        //@formatter:off
        String battleJson = "{\"combatants\": {}," +
                " \"nextStep\": null," +
                " \"logs\": [{\"timeMillis\": 1677480752836, \"message\": \"Battle created.\"}]}";

        //@formatter:on
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(storageBattleFile));
        writer2.write(battleJson);
        writer2.close();

        //@formatter:off
        String requestData = "{\"name\": \"Test Character\"}";

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
        assertTrue(jsonNode.get("message").isTextual());

        Battle battleObject = mapper.readValue(storageBattleFile, Battle.class);
        assertEquals(1, battleObject.getCombatants().size());
        assertNull(battleObject.getNextStep());
        assertEquals(2, battleObject.getLogs().size());
    }

    @Test
    void removeCombatantFromCurrentBattle() throws Exception {
        // Preload file with data.
        //@formatter:off
        String battleJson = "{\"combatants\": {\"Test Character\": {\"label\": \"Test Character\"," +
                " \"name\": \"Test Character\"," +
                " \"strength\": 14," +
                " \"dexterity\": 13," +
                " \"intelligence\": 12," +
                " \"health\": 11}," +
                " \"Another Character\": {\"label\": \"Another Character\"," +
                " \"name\": \"Another Character\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}," +
                " \"Another Character 2\": {\"label\": \"Another Character 2\"," +
                " \"name\": \"Another Character\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}}," +
                " \"nextStep\": null," +
                " \"logs\": [{\"timeMillis\": 1677430948958," +
                " \"message\": \"Battle created.\"}," +
                " {\"timeMillis\": 1677430966402," +
                " \"message\": \"Combatant Test Character added to battle.\"}," +
                " {\"timeMillis\": 1677430987182," +
                " \"message\": \"Combatant Another Character added to battle.\"}," +
                " {\"timeMillis\": 1677430989148," +
                " \"message\": \"Combatant Another Character 2 added to battle.\"}]}";
        //@formatter:on
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        String requestData = "{\"label\": \"Test Character\"}";

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
        assertTrue(jsonNode.get("message").isTextual());

        Battle battleObject = mapper.readValue(storageBattleFile, Battle.class);
        assertEquals(2, battleObject.getCombatants().size());
        assertNull(battleObject.getNextStep());
        assertEquals(5, battleObject.getLogs().size());
    }

    @Test
    void showBattle() throws Exception {
        // Preload file with data.
        //@formatter:off
        String battleJson = "{\"combatants\": {\"Test Character\": {\"label\": \"Test Character\"," +
                " \"name\": \"Test Character\"," +
                " \"strength\": 14," +
                " \"dexterity\": 13," +
                " \"intelligence\": 12," +
                " \"health\": 11}," +
                " \"Another Character\": {\"label\": \"Another Character\"," +
                " \"name\": \"Another Character\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}," +
                " \"Another Character 2\": {\"label\": \"Another Character 2\"," +
                " \"name\": \"Another Character\"," +
                " \"strength\": 10," +
                " \"dexterity\": 10," +
                " \"intelligence\": 10," +
                " \"health\": 10}}," +
                " \"nextStep\": null," +
                " \"logs\": [{\"timeMillis\": 1677430948958," +
                " \"message\": \"Battle created.\"}," +
                " {\"timeMillis\": 1677430966402," +
                " \"message\": \"Combatant Test Character added to battle.\"}," +
                " {\"timeMillis\": 1677430987182," +
                " \"message\": \"Combatant Another Character added to battle.\"}," +
                " {\"timeMillis\": 1677430989148," +
                " \"message\": \"Combatant Another Character 2 added to battle.\"}]}";
        //@formatter:on
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(get("/battle/show")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("message").isTextual());
        JsonNode battleJsonNode = jsonNode.get("battle");
        assertEquals(3, battleJsonNode.get("combatants").size());
        assertTrue(battleJsonNode.get("nextStep").isNull());
        assertTrue(battleJsonNode.get("logs").isArray());
        assertEquals(4, battleJsonNode.get("logs").size());
    }
}