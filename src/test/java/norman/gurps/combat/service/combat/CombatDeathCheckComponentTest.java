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

class CombatDeathCheckComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatDeathCheckComponentTest.class);
    CombatDeathCheckComponent component;
    CombatUtils utils;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatDeathCheckComponent(utils);
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
        target.setPreviousDamage(19);
        target.setCurrentDamage(21);
        target.setShockPenalty(-4);
        target.setNbrOfDeathChecksNeeded(3);
        target.setHealthStatus(HealthStatus.ALMOST3);
        target.setCurrentMove(2);
    }

    @Test
    void prompt() {
        NextStep nextStep = component.prompt(1, 0, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_DEATH_CHECK, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }

    @Test
    void validate_good() {
        assertDoesNotThrow(() -> component.validate(11));
    }

    @Test
    void validate_too_low() {
        assertThrows(LoggingException.class, () -> component.validate(2));
    }

    @Test
    void validate_too_high() {
        assertThrows(LoggingException.class, () -> component.validate(19));
    }

    @Test
    void updateTarget_success() {
        component.updateTarget(target, 10);

        assertEquals(2, target.getNbrOfDeathChecksNeeded());
        assertFalse(target.getDeathCheckFailed());
        assertEquals(HealthStatus.ALMOST3, target.getHealthStatus());
        assertEquals(2, target.getCurrentMove());
    }

    @Test
    void updateTarget_fail() {
        component.updateTarget(target, 11);

        assertEquals(0, target.getNbrOfDeathChecksNeeded());
        assertTrue(target.getDeathCheckFailed());
        assertEquals(HealthStatus.DEAD, target.getHealthStatus());
        assertEquals(0, target.getCurrentMove());
    }

    @Test
    void resolve_success_1() {
        target.setNbrOfDeathChecksNeeded(2);
        target.setDeathCheckFailed(false);
        target.setHealthStatus(HealthStatus.ALMOST3);
        target.setCurrentMove(2);

        NextStep nextStep = component.resolve(1, 0, target, 10);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DEATH_CHECK, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }

    @Test
    void resolve_success_3() {
        target.setNbrOfDeathChecksNeeded(0);
        target.setDeathCheckFailed(false);
        target.setHealthStatus(HealthStatus.ALMOST3);
        target.setCurrentMove(2);

        NextStep nextStep = component.resolve(1, 0, target, 10);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }

    @Test
    void resolve_fail() {
        target.setNbrOfDeathChecksNeeded(0);
        target.setDeathCheckFailed(true);
        target.setHealthStatus(HealthStatus.DEAD);
        target.setCurrentMove(0);

        NextStep nextStep = component.resolve(1, 0, target, 11);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }
}
