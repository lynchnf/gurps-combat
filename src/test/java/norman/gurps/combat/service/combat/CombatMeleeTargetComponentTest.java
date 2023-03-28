package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
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

class CombatMeleeTargetComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatMeleeTargetComponentTest.class);
    CombatMeleeTargetComponent component;
    CombatUtils utils;
    Combatant attacker;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatMeleeTargetComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setActionType(ActionType.ATTACK_MELEE);
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
    }

    @Test
    void prompt() {
        NextStep nextStep = component.prompt(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_MELEE_TARGET, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
    }

    @Test
    void validate() {
        List<Combatant> combatants = Arrays.asList(attacker, target);

        assertDoesNotThrow(() -> component.validate("Grunt", "Broadsword", "swing", attacker, combatants));
    }

    @Test
    void updateAttacker() {
        component.updateAttacker(attacker, "Grunt", "Broadsword", "swing");

        assertEquals("Grunt", attacker.getCombatMelees().get(0).getTargetLabel());
        assertEquals("Broadsword", attacker.getCombatMelees().get(0).getWeaponName());
        assertEquals("swing", attacker.getCombatMelees().get(0).getWeaponModeName());
    }

    @Test
    void resolve() {
        CombatMelee combatMelee = new CombatMelee();
        combatMelee.setTargetLabel("Grunt");
        combatMelee.setWeaponName("Broadsword");
        combatMelee.setWeaponModeName("swing");
        attacker.getCombatMelees().add(combatMelee);

        NextStep nextStep = component.resolve(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_TO_HIT, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Broadsword"));
    }
}
