package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatUnconsciousnessCheckComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatBeginTurnComponentTest.class);
    CombatUnconsciousnessCheckComponent component;
    CombatUtils utils;
    Combatant attacker;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatUnconsciousnessCheckComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setPreviousDamage(15);
        attacker.setHealthStatus(HealthStatus.BARELY);
        attacker.setCurrentMove(3);
        attacker.setShockPenalty(-4);
        attacker.setActionType(null);
    }

    @Test
    void prompt() {
        NextStep nextStep = component.prompt(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_UNCONSCIOUSNESS_CHECK, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }

    @Test
    void validate_good() {
        assertDoesNotThrow(() -> component.validate(13));
    }

    @Test
    void validate_bad() {
        assertThrows(LoggingException.class, () -> component.validate(2));
    }

    @Test
    void updateAttacker_success() {
        component.updateAttacker(attacker, 13);

        assertFalse(attacker.getUnconsciousnessCheckFailed());
        assertEquals(HealthStatus.BARELY, attacker.getHealthStatus());
        assertEquals(3, attacker.getCurrentMove());
    }

    @Test
    void updateAttacker_fail() {
        component.updateAttacker(attacker, 14);

        assertTrue(attacker.getUnconsciousnessCheckFailed());
        assertEquals(HealthStatus.UNCONSCIOUS, attacker.getHealthStatus());
        assertEquals(0, attacker.getCurrentMove());
    }

    @Test
    void resolve_success() {
        NextStep nextStep = component.resolve(1, 0, attacker, 13);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_ACTION, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }

    @Test
    void resolve_fail() {
        attacker.setHealthStatus(HealthStatus.UNCONSCIOUS);
        attacker.setCurrentMove(0);

        NextStep nextStep = component.resolve(1, 0, attacker, 14);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }
}
