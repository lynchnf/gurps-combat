package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatDefense;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.HealthStatus;
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

class CombatForDamageComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatForDamageComponentTest.class);
    CombatForDamageComponent component;
    CombatUtils utils;
    Combatant attacker;
    Combatant target;

    @BeforeEach
    void setUp() {
        utils = new CombatUtils();
        component = new CombatForDamageComponent(utils);
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
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatDefense.setToDefendResult(ResultType.FAILURE);
        target.getCombatDefenses().add(combatDefense);
    }

    @Test
    void prompt() {
        NextStep nextStep = component.promptAndUpdateAttacker(1, 0, attacker);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_DAMAGE, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Bob the Example"));

        assertEquals(2, attacker.getCombatMelees().get(0).getDamageDice());
        assertEquals(1, attacker.getCombatMelees().get(0).getDamageAdds());
        assertEquals(DamageType.CUTTING, attacker.getCombatMelees().get(0).getDamageType());
    }

    @Test
    void validate_good() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);

        assertDoesNotThrow(() -> component.validate(8, attacker));
    }

    @Test
    void validate_too_low() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);

        assertThrows(LoggingException.class, () -> component.validate(2, attacker));
    }

    @Test
    void validate_too_high() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);

        assertThrows(LoggingException.class, () -> component.validate(14, attacker));
    }

    @Test
    void updateAttacker_low_damage() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);

        component.updateAttacker(attacker, 3, target);

        assertEquals(3, attacker.getCombatMelees().get(0).getForDamageRoll());
        assertEquals(2, attacker.getCombatMelees().get(0).getTargetDamageResistance());
        assertEquals(1, attacker.getCombatMelees().get(0).getPenetratingDamage());
        assertEquals(1, attacker.getCombatMelees().get(0).getInjuryDamage());
    }

    @Test
    void updateAttacker_high_damage() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);

        component.updateAttacker(attacker, 13, target);

        assertEquals(13, attacker.getCombatMelees().get(0).getForDamageRoll());
        assertEquals(2, attacker.getCombatMelees().get(0).getTargetDamageResistance());
        assertEquals(11, attacker.getCombatMelees().get(0).getPenetratingDamage());
        assertEquals(16, attacker.getCombatMelees().get(0).getInjuryDamage());
    }

    @Test
    void updateAttacker_medium_damage() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);

        component.updateAttacker(attacker, 8, target);

        assertEquals(8, attacker.getCombatMelees().get(0).getForDamageRoll());
        assertEquals(2, attacker.getCombatMelees().get(0).getTargetDamageResistance());
        assertEquals(6, attacker.getCombatMelees().get(0).getPenetratingDamage());
        assertEquals(9, attacker.getCombatMelees().get(0).getInjuryDamage());
    }

    @Test
    void updateTarget_low_damage() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);
        attacker.getCombatMelees().get(0).setForDamageRoll(3);
        attacker.getCombatMelees().get(0).setTargetDamageResistance(2);
        attacker.getCombatMelees().get(0).setPenetratingDamage(1);
        attacker.getCombatMelees().get(0).setInjuryDamage(1);

        component.updateTarget(target, attacker);

        assertEquals(1, target.getCurrentDamage());
        assertEquals(-1, target.getShockPenalty());
        assertEquals(0, target.getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALIVE, target.getHealthStatus());
        assertEquals(3, target.getCurrentMove());
    }

    @Test
    void updateTarget_high_damage() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);
        attacker.getCombatMelees().get(0).setForDamageRoll(13);
        attacker.getCombatMelees().get(0).setTargetDamageResistance(2);
        attacker.getCombatMelees().get(0).setPenetratingDamage(11);
        attacker.getCombatMelees().get(0).setInjuryDamage(16);

        component.updateTarget(target, attacker);

        assertEquals(16, target.getCurrentDamage());
        assertEquals(-4, target.getShockPenalty());
        assertEquals(0, target.getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.BARELY, target.getHealthStatus());
        assertEquals(2, target.getCurrentMove());
    }

    @Test
    void updateTarget_medium_damage() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);
        attacker.getCombatMelees().get(0).setForDamageRoll(8);
        attacker.getCombatMelees().get(0).setTargetDamageResistance(2);
        attacker.getCombatMelees().get(0).setPenetratingDamage(6);
        attacker.getCombatMelees().get(0).setInjuryDamage(9);

        component.updateTarget(target, attacker);

        assertEquals(9, target.getCurrentDamage());
        assertEquals(-4, target.getShockPenalty());
        assertEquals(0, target.getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.REELING, target.getHealthStatus());
        assertEquals(2, target.getCurrentMove());
    }

    @Test
    void resolve() {
        attacker.getCombatMelees().get(0).setDamageDice(2);
        attacker.getCombatMelees().get(0).setDamageAdds(1);
        attacker.getCombatMelees().get(0).setDamageType(DamageType.CUTTING);
        attacker.getCombatMelees().get(0).setForDamageRoll(8);
        attacker.getCombatMelees().get(0).setTargetDamageResistance(2);
        attacker.getCombatMelees().get(0).setPenetratingDamage(6);
        attacker.getCombatMelees().get(0).setInjuryDamage(9);
        target.setCurrentDamage(8);
        target.setShockPenalty(-4);
        target.setNbrOfDeathChecksNeeded(0);
        target.setHealthStatus(HealthStatus.REELING);
        target.setCurrentMove(2);

        NextStep nextStep = component.resolve(1, 0, attacker, target);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        String nextStepMessage = nextStep.getMessage();
        LOGGER.debug("nextStepMessage=\"" + nextStepMessage + "\"");
        assertTrue(StringUtils.contains(nextStepMessage, "Grunt"));
    }
}
