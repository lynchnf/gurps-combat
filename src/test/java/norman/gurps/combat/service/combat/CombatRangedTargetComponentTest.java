package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.CombatRanged;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.NextStep;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatRangedTargetComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatRangedTargetComponentTest.class);
    CombatRangedTargetComponent component;
    CombatUtils utils;
    Combatant attacker;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatRangedTargetComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setActionType(ActionType.ATTACK_RANGED);
        attacker.getReadyItems().clear();
        attacker.getReadyItems().add("Longbow");
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
    }

    @Test
    void prompt() {
        NextStep nextStep = component.prompt(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_RANGED_TARGET, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }

    @Test
    void validate() {
        List<Combatant> combatants = Arrays.asList(attacker, target);

        assertDoesNotThrow(() -> component.validate("Grunt", "Longbow", 10, attacker, combatants));
    }

    @Test
    void updateAttacker() {
        component.updateAttacker(attacker, "Grunt", "Longbow", 10);

        assertEquals("Grunt", attacker.getCombatRanged().getTargetLabel());
        assertEquals("Longbow", attacker.getCombatRanged().getWeaponName());
        assertEquals(0, attacker.getCombatRanged().getNbrRoundsAimed());
        assertEquals(10, attacker.getCombatRanged().getSpeedAndRange());
    }

    @Test
    void resolve() {
        CombatRanged combatRanged = new CombatRanged();
        combatRanged.setTargetLabel("Grunt");
        combatRanged.setWeaponName("Longbow");
        combatRanged.setNbrRoundsAimed(0);
        combatRanged.setSpeedAndRange(10);
        attacker.setCombatRanged(combatRanged);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_TO_HIT, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Longbow"));
    }
}
