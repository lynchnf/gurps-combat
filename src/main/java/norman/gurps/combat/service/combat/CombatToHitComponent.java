package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatAttack;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.CombatRanged;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.RangedWeapon;
import norman.gurps.combat.model.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatToHitComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatToHitComponent.class);
    private CombatUtils utils;

    public CombatToHitComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep promptAndUpdateAttacker(int round, int index, Combatant attacker) {
        int weaponSkill = 0;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            CombatMelee combatMelee = attacker.getCombatMelees().get(size - 1);
            String weaponName = combatMelee.getWeaponName();
            MeleeWeapon weapon = utils.getMeleeWeapon(weaponName, attacker.getGameChar().getMeleeWeapons());

            // Base skill minus shock.
            weaponSkill = weapon.getSkill() + attacker.getShockPenalty();

            // Penalty for less than minimum strength.
            if (weapon.getMinimumStrength() > attacker.getGameChar().getStrength()) {
                weaponSkill -= (weapon.getMinimumStrength() - attacker.getGameChar().getStrength());
            }

            // All Out Attack bonus.
            if (attacker.getActionType() == ActionType.AOA_MELEE_4_TO_HIT) {
                weaponSkill += 4;

                // Wild swing penalty.
            } else if (attacker.getActionType() == ActionType.MOVE_ATTACK_MELEE) {
                weaponSkill -= 4;
                if (weaponSkill > 9) {
                    weaponSkill = 9;
                }
            }
            combatMelee.setToHitEffectiveSkill(weaponSkill);
        } else {
            CombatRanged combatRanged = attacker.getCombatRanged();
            String weaponName = combatRanged.getWeaponName();
            RangedWeapon weapon = utils.getRangedWeapon(weaponName, attacker.getGameChar().getRangedWeapons());

            // Base skill minus shock.
            weaponSkill = weapon.getSkill() + attacker.getShockPenalty();

            // Aim bonus.
            if (combatRanged.getNbrRoundsAimed() != null && combatRanged.getNbrRoundsAimed() >= 1) {
                weaponSkill += weapon.getAccuracy();
                if (combatRanged.getNbrRoundsAimed() == 2) {
                    weaponSkill += 1;
                } else if (combatRanged.getNbrRoundsAimed() >= 3) {
                    weaponSkill += 2;
                }
            }

            // Speed and range penalty.
            int speedAndRange = combatRanged.getSpeedAndRange();
            int speedAndRangePenalty = utils.getSpeedAndRangePenalty(speedAndRange);
            weaponSkill += speedAndRangePenalty;

            // All Out Attack bonus.
            if (attacker.getActionType() == ActionType.AOA_RANGED_1_TO_HIT) {
                weaponSkill += 1;

                // Wild shot penalty.
            } else if (attacker.getActionType() == ActionType.MOVE_ATTACK_RANGED) {
                int penalty = -2;
                if (weapon.getBulk() < -2) {
                    penalty = weapon.getBulk();
                }
                weaponSkill += penalty;
            }
            combatRanged.setToHitEffectiveSkill(weaponSkill);
        }

        String message = attacker.getLabel() + ", please roll 3d (need " + weaponSkill + " to hit).";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_TO_HIT);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(Integer toHitRoll) {
        // Validate to hit roll.
        if (toHitRoll == null) {
            throw new LoggingException(LOGGER, "To hit roll may not be blank.");
        } else if (toHitRoll < 3 || toHitRoll > 18) {
            throw new LoggingException(LOGGER, "To hit roll must be between 3 and 18.");
        }
    }

    public void updateAttacker(Combatant attacker, int toHitRoll) {
        CombatAttack combatAttack;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            combatAttack = attacker.getCombatMelees().get(size - 1);
        } else {
            combatAttack = attacker.getCombatRanged();
        }
        int weaponSkill = combatAttack.getToHitEffectiveSkill();
        combatAttack.setToHitRoll(toHitRoll);
        ResultType resultType = utils.getResultType(weaponSkill, toHitRoll);
        combatAttack.setToHitResultType(resultType);
    }

    public NextStep resolve(int round, int index, Combatant attacker, Combatant target) {
        CombatAttack combatAttack;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            combatAttack = attacker.getCombatMelees().get(size - 1);
        } else {
            combatAttack = attacker.getCombatRanged();
        }
        String targetLabel = combatAttack.getTargetLabel();
        String weaponName = combatAttack.getWeaponName();
        int weaponSkill = combatAttack.getToHitEffectiveSkill();
        int toHitRoll = combatAttack.getToHitRoll();
        ResultType resultType = combatAttack.getToHitResultType();

        CombatPhase combatPhase;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS) {
            combatPhase = CombatPhase.PROMPT_FOR_DAMAGE;
            message = attacker.getLabel() + " was critically successful in hitting " + targetLabel + " with " +
                    weaponName + ". Rolled a " + toHitRoll + ", needed a " + weaponSkill +
                    ". Please roll on the Critical Hit Table.";
        } else if (resultType == ResultType.SUCCESS) {
            if (target.getActionType() == ActionType.AOA_MELEE_4_TO_HIT ||
                    target.getActionType() == ActionType.AOA_MELEE_2_TO_DMG ||
                    target.getActionType() == ActionType.AOA_RANGED_1_TO_HIT) {
                combatPhase = CombatPhase.PROMPT_FOR_DAMAGE;
            } else {
                combatPhase = CombatPhase.PROMPT_FOR_DEFENSE;
            }
            message = attacker.getLabel() + " has successfully hit " + targetLabel + " with " + weaponName +
                    ". Rolled a " + toHitRoll + ", needed a " + weaponSkill + ".";
        } else if (resultType == ResultType.CRITICAL_FAILURE) {
            combatPhase = CombatPhase.END_TURN;
            message = attacker.getLabel() + " has critically failed to hit " + targetLabel + " with " + weaponName +
                    ". Rolled a " + toHitRoll + ", but needed a " + weaponSkill +
                    ". Please roll on the Critical Miss Table.";
        } else {
            combatPhase = CombatPhase.END_TURN;
            message =
                    attacker.getLabel() + " has failed to hit " + targetLabel + " with " + weaponName + ". Rolled a " +
                            toHitRoll + ", but needed a " + weaponSkill + ".";
        }

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }
}
