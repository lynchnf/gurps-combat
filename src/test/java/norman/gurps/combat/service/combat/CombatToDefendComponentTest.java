package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatToDefendComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatToDefendComponentTest.class);
    CombatToDefendComponent component;
    CombatUtils utils;
    Combatant attacker;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatToDefendComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setActionType(ActionType.ATTACK_MELEE);
        CombatMelee combatMelee = new CombatMelee();
        combatMelee.setTargetLabel("Grunt");
        combatMelee.setWeaponName("Broadsword");
        combatMelee.setWeaponModeName("swing");
        combatMelee.setToHitEffectiveSkill(14);
        combatMelee.setToHitRoll(10);
        combatMelee.setToHitResultType(ResultType.SUCCESS);
        attacker.getCombatMelees().add(combatMelee);
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        target.getCombatDefenses().add(combatDefense);
    }

    @Test
    void prompt() {
        NextStep nextStep = component.promptAndUpdateTarget(1, 0, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_DEFEND, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));

        assertEquals(10, target.getCombatDefenses().get(0).getToDefendEffectiveSkill());
    }

    @Test
    void validate() {
        assertDoesNotThrow(() -> component.validate(11));
    }

    @Test
    void updateTarget() {
        target.getCombatDefenses().get(0).setToDefendEffectiveSkill(10);

        component.updateTarget(target, 11);

        assertEquals(11, target.getCombatDefenses().get(0).getToDefendRoll());
        assertEquals(ResultType.FAILURE, target.getCombatDefenses().get(0).getToDefendResult());
    }

    @Test
    void resolve() {
        target.getCombatDefenses().get(0).setToDefendEffectiveSkill(10);
        target.getCombatDefenses().get(0).setToDefendRoll(11);
        target.getCombatDefenses().get(0).setToDefendResult(ResultType.FAILURE);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DAMAGE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Medium Shield"));
    }
}
