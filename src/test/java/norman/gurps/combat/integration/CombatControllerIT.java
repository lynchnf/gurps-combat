package norman.gurps.combat.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.config.GurpsCombatConfig;
import norman.gurps.combat.controller.CombatController;
import norman.gurps.combat.service.BattleService;
import norman.gurps.combat.service.CombatService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ContextConfiguration(
        classes = {CombatController.class, CombatService.class, BattleService.class, GameCharService.class,
                GurpsCombatConfig.class})
@WebMvcTest
class CombatControllerIT {
    @Value("${storage.dir.name}")
    String storageDirName;
    @Value("${storage.battle.file.name}")
    String storageBattleFileName;
    File storageBattleFile;
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
    }

    @AfterEach
    void tearDown() {
        if (storageBattleFile.exists()) {
            if (!storageBattleFile.delete()) {
                throw new RuntimeException("SETUP: Unable to delete file " + storageBattleFile + ".");
            }
        }
    }

    @Test
    void startCombat() throws Exception {
        // Preload file with data.
        String resourceName = "integration/combat-controller/combat-start/battle.json";
        String battleJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(1, jsonNode.get("messages").size());
        assertTrue(jsonNode.get("messages").get(0).isTextual());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("BEGIN", battleJsonNode.get("nextStep").get("phase").asText());
        assertFalse(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
    }

    @Test
    void nextStepInCombat_begin() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_begin.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_begin.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(2, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_ACTION", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
    }

    @Test
    void nextStepInCombat_resolve_action() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_resolve_action.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_resolve_action.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(2, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_TARGET_AND_WEAPON", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertEquals("ATTACK", battleJsonNode.get("combatants").get(0).get("action").asText());
    }

    @Test
    void nextStepInCombat_resolve_target_and_weapon() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_resolve_target_and_weapon.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_resolve_target_and_weapon.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(2, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_TO_HIT", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertEquals("ATTACK", battleJsonNode.get("combatants").get(0).get("action").asText());
        assertEquals("Grunt", battleJsonNode.get("combatants").get(0).get("targetLabel").asText());
        assertEquals("Broadsword", battleJsonNode.get("combatants").get(0).get("weaponName").asText());
        assertEquals("swing", battleJsonNode.get("combatants").get(0).get("modeName").asText());
        assertEquals(14, battleJsonNode.get("combatants").get(0).get("effectiveSkillToHit").asInt());
    }

    @Test
    void nextStepInCombat_resolve_to_hit() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_resolve_to_hit.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_resolve_to_hit.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(2, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_DEFENSE", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertEquals("ATTACK", battleJsonNode.get("combatants").get(0).get("action").asText());
        assertEquals("Grunt", battleJsonNode.get("combatants").get(0).get("targetLabel").asText());
        assertEquals("Broadsword", battleJsonNode.get("combatants").get(0).get("weaponName").asText());
        assertEquals("swing", battleJsonNode.get("combatants").get(0).get("modeName").asText());
        assertEquals(14, battleJsonNode.get("combatants").get(0).get("effectiveSkillToHit").asInt());
        assertEquals(10, battleJsonNode.get("combatants").get(0).get("rollToHit").asInt());
        assertEquals("SUCCESS", battleJsonNode.get("combatants").get(0).get("toHitResult").asText());
    }

    @Test
    void nextStepInCombat_resolve_defense() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_resolve_defense.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_resolve_defense.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(2, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_TO_DEFEND", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertEquals("ATTACK", battleJsonNode.get("combatants").get(0).get("action").asText());
        assertEquals("Grunt", battleJsonNode.get("combatants").get(0).get("targetLabel").asText());
        assertEquals("Broadsword", battleJsonNode.get("combatants").get(0).get("weaponName").asText());
        assertEquals("swing", battleJsonNode.get("combatants").get(0).get("modeName").asText());
        assertEquals(14, battleJsonNode.get("combatants").get(0).get("effectiveSkillToHit").asInt());
        assertEquals(10, battleJsonNode.get("combatants").get(0).get("rollToHit").asInt());
        assertEquals("SUCCESS", battleJsonNode.get("combatants").get(0).get("toHitResult").asText());
        assertEquals("BLOCK",
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("defenseType").asText());
        assertEquals("Medium Shield",
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("defendingItemName").asText());
        assertEquals(10,
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("effectiveSkillToDefend")
                        .asInt());
    }

    @Test
    void nextStepInCombat_resolve_to_defend() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_resolve_to_defend.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_resolve_to_defend.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(2, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_DAMAGE", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertEquals("ATTACK", battleJsonNode.get("combatants").get(0).get("action").asText());
        assertEquals("Grunt", battleJsonNode.get("combatants").get(0).get("targetLabel").asText());
        assertEquals("Broadsword", battleJsonNode.get("combatants").get(0).get("weaponName").asText());
        assertEquals("swing", battleJsonNode.get("combatants").get(0).get("modeName").asText());
        assertEquals(14, battleJsonNode.get("combatants").get(0).get("effectiveSkillToHit").asInt());
        assertEquals(10, battleJsonNode.get("combatants").get(0).get("rollToHit").asInt());
        assertEquals("SUCCESS", battleJsonNode.get("combatants").get(0).get("toHitResult").asText());
        assertEquals(2, battleJsonNode.get("combatants").get(0).get("damageDice").asInt());
        assertEquals(1, battleJsonNode.get("combatants").get(0).get("damageAdds").asInt());
        assertEquals("BLOCK",
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("defenseType").asText());
        assertEquals("Medium Shield",
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("defendingItemName").asText());
        assertEquals(10,
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("effectiveSkillToDefend")
                        .asInt());
        assertEquals(12,
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("rollToDefend").asInt());
        assertEquals("FAILURE",
                battleJsonNode.get("combatants").get(1).get("activeDefenses").get(0).get("toDefendResult").asText());
    }

    @Test
    void nextStepInCombat_resolve_damage() throws Exception {
        String resourceName1 = "integration/combat-controller/combat-next/request_resolve_damage.json";
        String requestData = readResource(resourceName1);

        // Preload file with data.
        String resourceName2 = "integration/combat-controller/combat-next/battle_resolve_damage.json";
        String battleJson = readResource(resourceName2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        //@formatter:off
        MvcResult result = mockMvc.perform(post("/combat/next")
                        .content(requestData)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //@formatter:on

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        assertTrue(jsonNode.get("successful").isBoolean());
        assertTrue(jsonNode.get("messages").isArray());
        assertEquals(3, jsonNode.get("messages").size());

        assertTrue(storageBattleFile.exists());
        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertTrue(battleJsonNode.get("combatants").isArray());
        assertEquals(2, battleJsonNode.get("combatants").size());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(1, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("RESOLVE_ACTION", battleJsonNode.get("nextStep").get("phase").asText());
        assertTrue(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertEquals("ATTACK", battleJsonNode.get("combatants").get(0).get("action").asText());
        assertEquals("Grunt", battleJsonNode.get("combatants").get(0).get("targetLabel").asText());
        assertEquals("Broadsword", battleJsonNode.get("combatants").get(0).get("weaponName").asText());
        assertEquals("swing", battleJsonNode.get("combatants").get(0).get("modeName").asText());
        assertEquals(14, battleJsonNode.get("combatants").get(0).get("effectiveSkillToHit").asInt());
        assertEquals(10, battleJsonNode.get("combatants").get(0).get("rollToHit").asInt());
        assertEquals("SUCCESS", battleJsonNode.get("combatants").get(0).get("toHitResult").asText());
        assertEquals(2, battleJsonNode.get("combatants").get(0).get("damageDice").asInt());
        assertEquals(1, battleJsonNode.get("combatants").get(0).get("damageAdds").asInt());
        assertEquals(8, battleJsonNode.get("combatants").get(0).get("rollForDamage").asInt());
        assertEquals(9, battleJsonNode.get("combatants").get(1).get("currentDamage").asInt());
        assertEquals("REELING", battleJsonNode.get("combatants").get(1).get("healthStatus").asText());
        assertEquals(2, battleJsonNode.get("combatants").get(1).get("currentMove").asInt());
        assertTrue(battleJsonNode.get("combatants").get(1).get("activeDefenses").isArray());
        assertTrue(battleJsonNode.get("combatants").get(1).get("activeDefenses").isEmpty());
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