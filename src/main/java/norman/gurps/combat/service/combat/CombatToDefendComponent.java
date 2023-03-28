package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatDefense;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.ResultType;
import norman.gurps.combat.model.Shield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatToDefendComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatToDefendComponent.class);
    private CombatUtils utils;

    public CombatToDefendComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep promptAndUpdateTarget(int round, int index, Combatant target) {
        int size = target.getCombatDefenses().size();
        CombatDefense combatDefense = target.getCombatDefenses().get(size - 1);
        DefenseType defenseType = combatDefense.getDefenseType();

        // Base defense skill.
        int defenseSkill = 0;
        if (defenseType == DefenseType.PARRY) {
            MeleeWeapon weapon = utils.getMeleeWeapon(combatDefense.getDefendingItemName(),
                    target.getGameChar().getMeleeWeapons());
            int weaponSkill = weapon.getSkill();
            defenseSkill = (weaponSkill / 2) + 3 + weapon.getParryModifier();
        } else if (defenseType == DefenseType.BLOCK) {
            Shield shield = utils.getShield(combatDefense.getDefendingItemName(), target.getGameChar().getShields());
            int shieldSkill = shield.getSkill();
            defenseSkill = (shieldSkill / 2) + 3;
        } else {
            defenseSkill = target.getCurrentMove() + 3;
        }

        // Defense bonus from ready shield.
        int defenseBonus = utils.getDefenseBonus(target.getReadyItems(), target.getGameChar().getShields());
        defenseSkill += defenseBonus;

        // All Out Attack bonus.
        if (target.getActionType() == ActionType.AOD_2_TO_DEFENSE) {
            defenseSkill += 2;
        }

        String message = target.getLabel() + ", please roll 3d (need " + defenseSkill + " to defend).";

        // Update target.
        combatDefense.setToDefendEffectiveSkill(defenseSkill);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_TO_DEFEND);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(Integer toDefendRoll) {
        // Validate to defend roll.
        if (toDefendRoll == null) {
            throw new LoggingException(LOGGER, "To defend roll may not be blank.");
        } else if (toDefendRoll < 3 || toDefendRoll > 18) {
            throw new LoggingException(LOGGER, "To defend roll must be between 3 and 18.");
        }
    }

    public void updateTarget(Combatant target, int toDefendRoll) {
        int size = target.getCombatDefenses().size();
        CombatDefense combatDefense = target.getCombatDefenses().get(size - 1);
        int defenseSkill = combatDefense.getToDefendEffectiveSkill();
        combatDefense.setToDefendRoll(toDefendRoll);
        ResultType resultType = utils.getResultType(defenseSkill, toDefendRoll);
        combatDefense.setToDefendResult(resultType);
    }

    public NextStep resolve(int round, int index, Combatant attacker, Combatant target) {
        int size = target.getCombatDefenses().size();
        CombatDefense combatDefense = target.getCombatDefenses().get(size - 1);
        DefenseType defenseType = combatDefense.getDefenseType();
        String defendingItemName = combatDefense.getDefendingItemName();
        int defenseSkill = combatDefense.getToDefendEffectiveSkill();
        int toDefendRoll = combatDefense.getToDefendRoll();
        ResultType resultType = combatDefense.getToDefendResult();

        String defenseDescription = "dodging";
        if (defenseType == DefenseType.PARRY) {
            defenseDescription = "parrying with his/her " + defendingItemName;
        } else if (defenseType == DefenseType.BLOCK) {
            defenseDescription = "blocking with his/her " + defendingItemName;
        }

        CombatPhase combatPhase;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS) {
            combatPhase = CombatPhase.END_TURN;
            message = target.getLabel() + " successfully defended against " + attacker.getLabel() + " by " +
                    defenseDescription + ". Rolled a " + toDefendRoll + ", needed a " + defenseSkill + ".";
            if (attacker.getActionType() == ActionType.ATTACK_MELEE ||
                    attacker.getActionType() == ActionType.AOA_MELEE_4_TO_HIT ||
                    attacker.getActionType() == ActionType.AOA_MELEE_2_TO_DMG ||
                    attacker.getActionType() == ActionType.MOVE_ATTACK_MELEE) {
                message += " Please roll on the Critical Miss Table for " + attacker.getLabel() + ".";
            }
        } else if (resultType == ResultType.SUCCESS) {
            combatPhase = CombatPhase.END_TURN;
            message = target.getLabel() + " successfully defended against " + attacker.getLabel() + " by " +
                    defenseDescription + ". Rolled a " + toDefendRoll + ", needed a " + defenseSkill + ".";
        } else if (resultType == ResultType.CRITICAL_FAILURE) {
            combatPhase = CombatPhase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " critically failed to defend against " + attacker.getLabel() + " by " +
                    defenseDescription + ". Rolled a " + toDefendRoll + ", but needed a " + defenseSkill + ".";
            if (defenseType == DefenseType.PARRY) {
                message += " Please roll on the Critical Miss Table.";
            } else if (defenseType == DefenseType.BLOCK) {
                message += " He/she lost his/her grip on his/her " + defendingItemName +
                        " and must take a turn to ready it before using it again.";
            } else {
                message += " He/she lost his/her footing and fell prone.";
            }
        } else {
            combatPhase = CombatPhase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " failed to defend against " + attacker.getLabel() + " by " +
                    defenseDescription + ". Rolled a " + toDefendRoll + ", but needed a " + defenseSkill + ".";
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
