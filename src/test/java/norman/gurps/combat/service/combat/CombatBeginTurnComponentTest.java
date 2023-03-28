package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatBeginTurnComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatBeginTurnComponentTest.class);
    CombatBeginTurnComponent component;
    CombatUtils utils;
    Combatant attacker;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatBeginTurnComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
    }

    @Test
    void updateAttacker_alive() {
        component.updateAttacker(attacker);

        assertEquals(0, attacker.getCurrentDamage());
        assertEquals(0, attacker.getPreviousDamage());
        assertEquals(HealthStatus.ALIVE, attacker.getHealthStatus());
        assertEquals(6, attacker.getCurrentMove());
        assertEquals(0, attacker.getShockPenalty());
        assertNull(attacker.getActionType());
    }

    @Test
    void updateAttacker_reeling() {
        attacker.setCurrentDamage(5);
        attacker.setPreviousDamage(6);

        component.updateAttacker(attacker);

        assertEquals(0, attacker.getCurrentDamage());
        assertEquals(11, attacker.getPreviousDamage());
        assertEquals(HealthStatus.REELING, attacker.getHealthStatus());
        assertEquals(3, attacker.getCurrentMove());
        assertEquals(-4, attacker.getShockPenalty());
        assertNull(attacker.getActionType());
    }

    @Test
    void updateAttacker_barely() {
        attacker.setCurrentDamage(5);
        attacker.setPreviousDamage(10);

        component.updateAttacker(attacker);

        assertEquals(0, attacker.getCurrentDamage());
        assertEquals(15, attacker.getPreviousDamage());
        assertEquals(HealthStatus.BARELY, attacker.getHealthStatus());
        assertEquals(3, attacker.getCurrentMove());
        assertEquals(-4, attacker.getShockPenalty());
        assertNull(attacker.getActionType());
    }

    @Test
    void updateAttacker_dead() {
        attacker.setCurrentDamage(5);
        attacker.setPreviousDamage(25);
        attacker.setDeathCheckFailed(true);

        component.updateAttacker(attacker);

        assertEquals(0, attacker.getCurrentDamage());
        assertEquals(30, attacker.getPreviousDamage());
        assertEquals(HealthStatus.DEAD, attacker.getHealthStatus());
        assertEquals(0, attacker.getCurrentMove());
        // assertEquals(-4, attacker.getShockPenalty()); We don't care about shock when we're dead.
        assertNull(attacker.getActionType());
    }

    @Test
    void resolve_alive() {
        attacker.setActionType(null);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_ACTION, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, HealthStatus.ALIVE.toString()));
    }

    @Test
    void resolve_reeling() {
        attacker.setPreviousDamage(11);
        attacker.setHealthStatus(HealthStatus.REELING);
        attacker.setCurrentMove(3);
        attacker.setShockPenalty(-4);
        attacker.setActionType(null);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_ACTION, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, HealthStatus.REELING.toString()));
    }

    @Test
    void resolve_barely() {
        attacker.setPreviousDamage(15);
        attacker.setHealthStatus(HealthStatus.BARELY);
        attacker.setCurrentMove(3);
        attacker.setShockPenalty(-4);
        attacker.setActionType(null);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_UNCONSCIOUSNESS_CHECK, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, HealthStatus.BARELY.toString()));
    }

    @Test
    void resolve_dead() {
        attacker.setPreviousDamage(30);
        attacker.setDeathCheckFailed(true);
        attacker.setHealthStatus(HealthStatus.DEAD);
        attacker.setCurrentMove(0);
        // attacker.setShockPenalty(-4);
        attacker.setActionType(null);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, HealthStatus.DEAD.toString()));
    }
}
