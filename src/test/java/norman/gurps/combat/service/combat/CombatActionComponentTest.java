package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
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

class CombatActionComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatActionComponentTest.class);
    CombatActionComponent component;
    Combatant attacker;

    @BeforeEach
    void setUp() {
        component = new CombatActionComponent();
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setActionType(null);
    }

    @Test
    void prompt() {
        NextStep nextStep = component.prompt(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_ACTION, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }

    @Test
    void validate_good() {
        assertDoesNotThrow(() -> component.validate(ActionType.ATTACK_MELEE));
    }

    @Test
    void validate_bad() {
        assertThrows(LoggingException.class, () -> component.validate(null));
    }

    @Test
    void updateAttacker() {
        component.updateAttacker(attacker, ActionType.ATTACK_MELEE);

        assertEquals(ActionType.ATTACK_MELEE, attacker.getActionType());
    }

    @Test
    void resolve_attack() {
        attacker.setActionType(ActionType.ATTACK_MELEE);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_MELEE_TARGET, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, ActionType.ATTACK_MELEE.toString()));
    }

    @Test
    void resolve_do_nothing() {
        attacker.setActionType(ActionType.DO_NOTHING);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, ActionType.DO_NOTHING.toString()));
    }
}
