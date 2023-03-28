package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.CombatRanged;
import norman.gurps.combat.model.Combatant;
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

class CombatToHitComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatToHitComponentTest.class);
    CombatToHitComponent component;
    CombatUtils utils;
    Combatant attacker;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatToHitComponent(utils);
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
        attacker.setActionType(ActionType.ATTACK_MELEE);
        CombatMelee combatMelee = new CombatMelee();
        combatMelee.setTargetLabel("Grunt");
        combatMelee.setWeaponName("Broadsword");
        combatMelee.setWeaponModeName("swing");
        attacker.getCombatMelees().add(combatMelee);
        target = TestHelper.getCombatant(TestHelper.getGameChar2());
    }

    @Test
    void prompt_regular_melee() {
        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(14, attacker.getCombatMelees().get(0).getToHitEffectiveSkill());
    }

    @Test
    void prompt_all_out_melee() {
        attacker.setActionType(ActionType.AOA_MELEE_4_TO_HIT);

        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(18, attacker.getCombatMelees().get(0).getToHitEffectiveSkill());
    }

    @Test
    void prompt_melee_with_shock() {
        attacker.setShockPenalty(-4);

        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(10, attacker.getCombatMelees().get(0).getToHitEffectiveSkill());
    }

    @Test
    void prompt_melee_wild_swing() {
        attacker.setActionType(ActionType.MOVE_ATTACK_MELEE);

        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(9, attacker.getCombatMelees().get(0).getToHitEffectiveSkill());
    }

    @Test
    void prompt_regular_ranged() {
        attacker.setActionType(ActionType.ATTACK_RANGED);
        attacker.getCombatMelees().clear();
        CombatRanged combatRanged = new CombatRanged();
        combatRanged.setTargetLabel("Grunt");
        combatRanged.setWeaponName("Longbow");
        combatRanged.setSpeedAndRange(10);
        attacker.setCombatRanged(combatRanged);

        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(9, attacker.getCombatRanged().getToHitEffectiveSkill());
    }

    @Test
    void prompt_ranged_with_aim() {
        attacker.setActionType(ActionType.ATTACK_RANGED);
        attacker.getCombatMelees().clear();
        CombatRanged combatRanged = new CombatRanged();
        combatRanged.setTargetLabel("Grunt");
        combatRanged.setWeaponName("Longbow");
        combatRanged.setNbrRoundsAimed(1);
        combatRanged.setSpeedAndRange(10);
        attacker.setCombatRanged(combatRanged);

        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(12, attacker.getCombatRanged().getToHitEffectiveSkill());
    }

    @Test
    void prompt_ranged_wild_shot() {
        attacker.setActionType(ActionType.MOVE_ATTACK_RANGED);
        attacker.getCombatMelees().clear();
        CombatRanged combatRanged = new CombatRanged();
        combatRanged.setTargetLabel("Grunt");
        combatRanged.setWeaponName("Longbow");
        combatRanged.setSpeedAndRange(10);
        attacker.setCombatRanged(combatRanged);

        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(1, attacker.getCombatRanged().getToHitEffectiveSkill());
    }

    @Test
    void validate_good() {
        assertDoesNotThrow(() -> component.validate(10));
    }

    @Test
    void validate_bad() {
        assertThrows(LoggingException.class, () -> component.validate(2));
    }

    @Test
    void updateAttacker() {
        attacker.getCombatMelees().get(0).setToHitEffectiveSkill(14);

        component.updateAttacker(attacker, 10);

        assertEquals(10, attacker.getCombatMelees().get(0).getToHitRoll());
        assertEquals(ResultType.SUCCESS, attacker.getCombatMelees().get(0).getToHitResultType());
    }

    @Test
    void resolve_success() {
        attacker.getCombatMelees().get(0).setToHitEffectiveSkill(14);
        attacker.getCombatMelees().get(0).setToHitRoll(10);
        attacker.getCombatMelees().get(0).setToHitResultType(ResultType.SUCCESS);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DEFENSE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Broadsword"));
    }

    @Test
    void resolve_success_no_defense() {
        attacker.getCombatMelees().get(0).setToHitEffectiveSkill(14);
        attacker.getCombatMelees().get(0).setToHitRoll(10);
        attacker.getCombatMelees().get(0).setToHitResultType(ResultType.SUCCESS);
        target.setActionType(ActionType.AOA_MELEE_4_TO_HIT);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DAMAGE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Broadsword"));
    }

    @Test
    void resolve_critical_success() {
        attacker.getCombatMelees().get(0).setToHitEffectiveSkill(14);
        attacker.getCombatMelees().get(0).setToHitRoll(4);
        attacker.getCombatMelees().get(0).setToHitResultType(ResultType.CRITICAL_SUCCESS);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DAMAGE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Broadsword"));
    }

    @Test
    void resolve_failure() {
        attacker.getCombatMelees().get(0).setToHitEffectiveSkill(14);
        attacker.getCombatMelees().get(0).setToHitRoll(15);
        attacker.getCombatMelees().get(0).setToHitResultType(ResultType.FAILURE);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Broadsword"));
    }

    @Test
    void resolve_critical_failure() {
        attacker.getCombatMelees().get(0).setToHitEffectiveSkill(14);
        attacker.getCombatMelees().get(0).setToHitRoll(17);
        attacker.getCombatMelees().get(0).setToHitResultType(ResultType.CRITICAL_FAILURE);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
        assertTrue(StringUtils.contains(nextStepMessage, "Broadsword"));
    }
}
