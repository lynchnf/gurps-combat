package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.CombatRanged;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.NextStep;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatAimTargetComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatAimTargetComponentTest.class);
    CombatAimTargetComponent component;
    CombatUtils utils;
    Combatant attacker;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatAimTargetComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setActionType(ActionType.AIM);
        attacker.getReadyItems().clear();
        attacker.getReadyItems().add("Longbow");
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
    }

    @Test
    void prompt() {
        NextStep nextStep = component.prompt(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_AIM_TARGET, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }

    @Test
    void validate_good() {
        List<Combatant> combatants = Arrays.asList(attacker, target);

        assertDoesNotThrow(() -> component.validate("Grunt", "Longbow", attacker, combatants));
    }

    @Test
    void validate_bad() {
        GameChar gameChar2 = TestHelper.getGameChar2();
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add(attacker.getLabel());
        existingLabels.add(target.getLabel());
        Combatant other = TestHelper.getCombatant(gameChar2, existingLabels);
        List<Combatant> combatants = Arrays.asList(attacker, target, other);
        CombatRanged combatRanged = new CombatRanged();
        combatRanged.setTargetLabel(other.getLabel());
        combatRanged.setWeaponName("Longbow");
        combatRanged.setNbrRoundsAimed(1);
        attacker.setCombatRanged(combatRanged);

        assertThrows(LoggingException.class, () -> component.validate("Grunt", "Longbow", attacker, combatants));
    }

    @Test
    void updateAttacker() {
        component.updateAttacker(attacker, "Grunt", "Longbow");

        assertEquals("Grunt", attacker.getCombatRanged().getTargetLabel());
        assertEquals("Longbow", attacker.getCombatRanged().getWeaponName());
        assertEquals(1, attacker.getCombatRanged().getNbrRoundsAimed());
    }

    @Test
    void resolve() {
        CombatRanged combatRanged = new CombatRanged();
        combatRanged.setTargetLabel("Grunt");
        combatRanged.setWeaponName("Longbow");
        combatRanged.setNbrRoundsAimed(1);
        attacker.setCombatRanged(combatRanged);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Longbow"));
    }
}
