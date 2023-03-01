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
        assertTrue(jsonNode.get("message").isTextual());

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
        // Preload storage with a battle.
        //@formatter:off
        String battleJson = "{\"combatants\":[]," +
                "\"nextStep\":null," +
                "\"logs\":[{\"timeMillis\":1677627401631," +
                "\"message\":\"Battle created.\"}]}";
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
    void addStoredCharacterToCurrentBattle_empty_battle() throws Exception {
        // Create game character in storage
        //@formatter:off
        String gameCharJson = "[{\"name\":\"Test Character\"," +
                "\"strength\":14," +
                "\"dexterity\":13," +
                "\"intelligence\":12," +
                "\"health\":11," +
                "\"hitPoints\":15," +
                "\"basicSpeed\":6.25," +
                "\"meleeWeapons\":[{\"name\":\"Broadsword\"," +
                "\"skill\":13," +
                "\"modes\":[{\"name\":\"swing\"," +
                "\"damageDice\":2," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CUTTING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}," +
                "{\"name\":\"thrust\"," +
                "\"damageDice\":1," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CRUSHING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}]," +
                "\"minStrength\":10}]," +
                "\"shield\":{\"name\":\"Medium Shield\"," +
                "\"skill\":13," +
                "\"defenseBonus\":2}," +
                "\"armorList\":[{\"location\":\"TORSO\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"GROIN\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"LEGS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"ARMS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"SKULL\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FACE\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"HANDS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FEET\"," +
                "\"damageResistance\":2}]}]";
        //@formatter:on
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        // Create empty battle in storage.
        //@formatter:off
        String battleJson = "{\"combatants\":[]," +
                "\"nextStep\":null," +
                "\"logs\":[{\"timeMillis\":1677627401631," +
                "\"message\":\"Battle created.\"}]}";
        //@formatter:on
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
        assertTrue(jsonNode.get("message").isTextual());

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
        //@formatter:off
        String gameCharJson = "[{\"name\":\"Test Character\"," +
                "\"strength\":14," +
                "\"dexterity\":13," +
                "\"intelligence\":12," +
                "\"health\":11," +
                "\"hitPoints\":15," +
                "\"basicSpeed\":6.25," +
                "\"meleeWeapons\":[{\"name\":\"Broadsword\"," +
                "\"skill\":13," +
                "\"modes\":[{\"name\":\"swing\"," +
                "\"damageDice\":2," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CUTTING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}," +
                "{\"name\":\"thrust\"," +
                "\"damageDice\":1," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CRUSHING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}]," +
                "\"minStrength\":10}]," +
                "\"shield\":{\"name\":\"Medium Shield\"," +
                "\"skill\":13," +
                "\"defenseBonus\":2}," +
                "\"armorList\":[{\"location\":\"TORSO\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"GROIN\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"LEGS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"ARMS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"SKULL\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FACE\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"HANDS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FEET\"," +
                "\"damageResistance\":2}]}]";
        //@formatter:on
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageGameCharFile));
        writer.write(gameCharJson);
        writer.close();

        // Create battle with combatant in storage.
        //@formatter:off
        String battleJson = "{\"combatants\":[{\"label\":\"Test Character\"," +
                "\"gameChar\":{\"name\":\"Test Character\"," +
                "\"strength\":14," +
                "\"dexterity\":13," +
                "\"intelligence\":12," +
                "\"health\":11," +
                "\"hitPoints\":15," +
                "\"basicSpeed\":6.25," +
                "\"meleeWeapons\":[{\"name\":\"Broadsword\"," +
                "\"skill\":13," +
                "\"modes\":[{\"name\":\"swing\"," +
                "\"damageDice\":2," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CUTTING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}," +
                "{\"name\":\"thrust\"," +
                "\"damageDice\":1," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CRUSHING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}]," +
                "\"minStrength\":10}]," +
                "\"shield\":{\"name\":\"Medium Shield\"," +
                "\"skill\":13," +
                "\"defenseBonus\":2}," +
                "\"armorList\":[{\"location\":\"TORSO\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"GROIN\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"LEGS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"ARMS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"SKULL\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FACE\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"HANDS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FEET\"," +
                "\"damageResistance\":2}]}," +
                "\"damageTaken\":0}]," +
                "\"nextStep\":null," +
                "\"logs\":[{\"timeMillis\":1677627401631," +
                "\"message\":\"Battle created.\"}," +
                "{\"timeMillis\":1677629888408," +
                "\"message\":\"Combatant Test Character added to Battle.\"}]}";
        //@formatter:on
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
        assertTrue(jsonNode.get("message").isTextual());

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
        //@formatter:off
        String battleJson = "{\"combatants\":[{\"label\":\"Test Character\"," +
                "\"gameChar\":{\"name\":\"Test Character\"," +
                "\"strength\":14," +
                "\"dexterity\":13," +
                "\"intelligence\":12," +
                "\"health\":11," +
                "\"hitPoints\":15," +
                "\"basicSpeed\":6.25," +
                "\"meleeWeapons\":[{\"name\":\"Broadsword\"," +
                "\"skill\":13," +
                "\"modes\":[{\"name\":\"swing\"," +
                "\"damageDice\":2," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CUTTING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}," +
                "{\"name\":\"thrust\"," +
                "\"damageDice\":1," +
                "\"damageAdds\":1," +
                "\"damageType\":\"CRUSHING\"," +
                "\"reaches\":[1]," +
                "\"parryType\":\"YES\"," +
                "\"parryModifier\":0}]," +
                "\"minStrength\":10}]," +
                "\"shield\":{\"name\":\"Medium Shield\"," +
                "\"skill\":13," +
                "\"defenseBonus\":2}," +
                "\"armorList\":[{\"location\":\"TORSO\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"GROIN\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"LEGS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"ARMS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"SKULL\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FACE\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"HANDS\"," +
                "\"damageResistance\":2}," +
                "{\"location\":\"FEET\"," +
                "\"damageResistance\":2}]}," +
                "\"damageTaken\":0}]," +
                "\"nextStep\":null," +
                "\"logs\":[{\"timeMillis\":1677627401631," +
                "\"message\":\"Battle created.\"}," +
                "{\"timeMillis\":1677629888408," +
                "\"message\":\"Combatant Test Character added to Battle.\"}]}";
        //@formatter:on
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
        assertTrue(jsonNode.get("message").isTextual());

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
        //@formatter:off
        String battleJson = "{\"combatants\":[]," +
                "\"nextStep\":null," +
                "\"logs\":[{\"timeMillis\":1677627401631," +
                "\"message\":\"Battle created.\"}]}";
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

        System.out.println("result=" + result);
        System.out.println("response=" + result.getResponse());
        System.out.println("content=" + result.getResponse().getContentAsString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("successful").asBoolean());
        assertTrue(jsonNode.get("message").isTextual());
        assertTrue(jsonNode.get("battle").get("combatants").isArray());
        assertTrue(jsonNode.get("battle").get("logs").isArray());
    }
}