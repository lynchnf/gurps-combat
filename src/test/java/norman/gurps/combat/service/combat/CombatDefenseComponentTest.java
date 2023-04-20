package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatDefense;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.ResultType;
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

class CombatDefenseComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatDefenseComponentTest.class);
    CombatDefenseComponent component;
    CombatUtils utils;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatDefenseComponent(utils);
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
    }

    @Test
    void prompt_regular() {
        NextStep nextStep = component.prompt(1, 0, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_DEFENSE, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }

    @Test
    void prompt_all_out_attack() {
        target.setActionType(ActionType.AOA_MELEE_4_TO_HIT);

        NextStep nextStep = component.prompt(1, 0, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DAMAGE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }

    @Test
    void validate_parry_good() {
        assertDoesNotThrow(() -> component.validate(DefenseType.PARRY, "Axe", target));
    }

    @Test
    void validate_parry_bad_unbalanced() {
        CombatMelee combatMelee = new CombatMelee();
        combatMelee.setTargetLabel("Bob the Example");
        combatMelee.setWeaponName("Axe");
        combatMelee.setWeaponModeName("swing");
        combatMelee.setToHitEffectiveSkill(12);
        combatMelee.setToHitRoll(13);
        combatMelee.setToHitResultType(ResultType.FAILURE);
        target.getCombatMelees().add(combatMelee);

        assertThrows(LoggingException.class, () -> component.validate(DefenseType.PARRY, "Axe", target));
    }

    @Test
    void validate_block_good() {
        assertDoesNotThrow(() -> component.validate(DefenseType.BLOCK, "Medium Shield", target));
    }

    @Test
    void validate_block_bad() {
        assertThrows(LoggingException.class, () -> component.validate(DefenseType.BLOCK, "Axe", target));
    }

    @Test
    void validate_dodge_good() {
        assertDoesNotThrow(() -> component.validate(DefenseType.DODGE, null, target));
    }

    @Test
    void validate_dodge_bad() {
        assertThrows(LoggingException.class, () -> component.validate(DefenseType.DODGE, "Axe", target));
    }

    @Test
    void updateTarget() {
        component.updateTarget(target, DefenseType.PARRY, "Axe");

        assertEquals(DefenseType.PARRY, target.getCombatDefenses().get(0).getDefenseType());
        assertEquals("Axe", target.getCombatDefenses().get(0).getDefendingItemName());
    }

    @Test
    void resolve() {
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        target.getCombatDefenses().add(combatDefense);

        NextStep nextStep = component.resolve(1, 0, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_TO_DEFEND, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Medium Shield"));
    }
}
