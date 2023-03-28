package norman.gurps.combat.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.config.GurpsCombatConfig;
import norman.gurps.combat.controller.CombatController;
import norman.gurps.combat.service.BattleService;
import norman.gurps.combat.service.CombatService;
import norman.gurps.combat.service.GameCharService;
import norman.gurps.combat.service.combat.CombatActionComponent;
import norman.gurps.combat.service.combat.CombatAimTargetComponent;
import norman.gurps.combat.service.combat.CombatBeginTurnComponent;
import norman.gurps.combat.service.combat.CombatDeathCheckComponent;
import norman.gurps.combat.service.combat.CombatDefenseComponent;
import norman.gurps.combat.service.combat.CombatEndTurnComponent;
import norman.gurps.combat.service.combat.CombatForDamageComponent;
import norman.gurps.combat.service.combat.CombatMeleeTargetComponent;
import norman.gurps.combat.service.combat.CombatRangedTargetComponent;
import norman.gurps.combat.service.combat.CombatToDefendComponent;
import norman.gurps.combat.service.combat.CombatToHitComponent;
import norman.gurps.combat.service.combat.CombatUnconsciousnessCheckComponent;
import norman.gurps.combat.service.combat.CombatUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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
        classes = {CombatController.class, CombatService.class, BattleService.class, CombatBeginTurnComponent.class,
                CombatUnconsciousnessCheckComponent.class, CombatActionComponent.class,
                CombatMeleeTargetComponent.class, CombatAimTargetComponent.class, CombatRangedTargetComponent.class,
                CombatToHitComponent.class, CombatDefenseComponent.class, CombatToDefendComponent.class,
                CombatForDamageComponent.class, CombatDeathCheckComponent.class, CombatEndTurnComponent.class,
                CombatUtils.class, GameCharService.class, GurpsCombatConfig.class})
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
                throw new RuntimeException("TEARDOWN: Unable to delete file " + storageBattleFile + ".");
            }
        }
    }

    @Test
    void startCombat() throws Exception {
        // Create battle in storage.
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
        assertTrue(jsonNode.get("successful").asBoolean());
        assertEquals(1, jsonNode.get("messages").size());

        JsonNode battleJsonNode = mapper.readTree(storageBattleFile);
        assertEquals(3, battleJsonNode.get("combatants").size());
        assertEquals("Bob the Example", battleJsonNode.get("combatants").get(0).get("label").asText());
        assertEquals("Grunt", battleJsonNode.get("combatants").get(1).get("label").asText());
        assertEquals("Grunt 2", battleJsonNode.get("combatants").get(2).get("label").asText());
        assertEquals(1, battleJsonNode.get("nextStep").get("round").asInt());
        assertEquals(0, battleJsonNode.get("nextStep").get("index").asInt());
        assertEquals("BEGIN_TURN", battleJsonNode.get("nextStep").get("combatPhase").asText());
        assertFalse(battleJsonNode.get("nextStep").get("inputNeeded").asBoolean());
        assertTrue(battleJsonNode.get("nextStep").get("message").isNull());
        assertEquals(5, battleJsonNode.get("battleLogs").size());
    }

    @Test
    void nextStepInCombat_melee() throws Exception {
        String resourceName = "integration/combat-controller/combat-next/battle_melee.json";
        String resourceName1 = "integration/combat-controller/combat-next/requests_melee.json";
        String resourceName2 = "integration/combat-controller/combat-next/responses_melee.json";

        doNextStepInCombat(resourceName, resourceName1, resourceName2);
    }

    @Test
    void nextStepInCombat_ranged() throws Exception {
        String resourceName = "integration/combat-controller/combat-next/battle_ranged.json";
        String resourceName1 = "integration/combat-controller/combat-next/requests_ranged.json";
        String resourceName2 = "integration/combat-controller/combat-next/responses_ranged.json";

        doNextStepInCombat(resourceName, resourceName1, resourceName2);
    }

    private void doNextStepInCombat(String resourceName, String resourceName1, String resourceName2) throws Exception {
        String battleJson = readResource(resourceName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(storageBattleFile));
        writer.write(battleJson);
        writer.close();

        ObjectMapper mapper = new ObjectMapper();

        String requestsJson = readResource(resourceName1);
        JsonNode requests = mapper.readTree(requestsJson);

        String responsesJson = readResource(resourceName2);
        JsonNode responses = mapper.readTree(responsesJson);

        for (int i = 0; i < requests.size(); i++) {
            String requestData = requests.get(i).toString();

            //@formatter:off
            MvcResult result = mockMvc.perform(post("/combat/next")
                            .content(requestData)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            //@formatter:on

            String message = "Test failed at step " + i + ".";
            String expectedStr = responses.get(i).toString();
            String actualStr = result.getResponse().getContentAsString();
            JSONAssert.assertEquals(message, expectedStr, actualStr, JSONCompareMode.STRICT);
        }
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
