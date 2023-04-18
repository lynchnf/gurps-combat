package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatDefense;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.ParryType;
import norman.gurps.combat.model.Shield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatDefenseComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatDefenseComponent.class);
    private CombatUtils utils;

    public CombatDefenseComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep prompt(int round, int index, Combatant target) {
        CombatPhase combatPhase = CombatPhase.RESOLVE_DEFENSE;
        String message = target.getLabel() + ", please chose a defense and item (if needed for defense).";
        boolean inputNeeded = true;
        if (target.getActionType() == ActionType.AOA_MELEE_4_TO_HIT ||
                target.getActionType() == ActionType.AOA_MELEE_2_TO_DMG ||
                target.getActionType() == ActionType.AOA_RANGED_1_TO_HIT) {
            combatPhase = CombatPhase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " does not get a defense because he/she took an all-out attack action.";
            inputNeeded = false;
        }

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(inputNeeded);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(DefenseType defenseType, String defendingItemName, Combatant target) {
        // Validate defense type.
        if (defenseType == null) {
            throw new LoggingException(LOGGER, "Defense may not be blank.");
        }

        // Validate defending item name.
        if ((defenseType == DefenseType.PARRY || defenseType == DefenseType.BLOCK) && defendingItemName == null) {
            throw new LoggingException(LOGGER, "If Defense is parry or block, defending item name may not be blank.");
        } else if (defenseType != DefenseType.PARRY && defenseType != DefenseType.BLOCK && defendingItemName != null) {
            throw new LoggingException(LOGGER,
                    "If Defense is not parry and not block, defending item name must be blank.");

            // Validate weapon for parry.
        } else if (defenseType == DefenseType.PARRY) {
            if (!target.getReadyItems().contains(defendingItemName)) {
                throw new LoggingException(LOGGER,
                        "Melee weapon " + defendingItemName + " is not a ready item for defending combatant " +
                                target.getLabel() + ".");
            } else {
                MeleeWeapon weapon = utils.getMeleeWeapon(defendingItemName, target.getGameChar().getMeleeWeapons());
                if (weapon == null) {
                    throw new LoggingException(LOGGER, "Unexpected error. Melee weapon " + defendingItemName +
                            " not found in inventory of combatant " + target.getLabel() + ".");
                }

                if (weapon.getParryType() == ParryType.NO) {
                    throw new LoggingException(LOGGER,
                            "Melee weapon " + defendingItemName + " cannot be used to parry.");
                } else if (weapon.getParryType() == ParryType.UNBALANCED &&
                        utils.getCombatMelee(defendingItemName, target.getCombatMelees()) != null) {
                    throw new LoggingException(LOGGER, "Melee weapon " + defendingItemName +
                            " is an unbalanced weapon and cannot be used to parry because " + target.getLabel() +
                            " already used it to attack this round.");
                }
            }

            // Validate shield for block.
        } else if (defenseType == DefenseType.BLOCK) {
            if (!target.getReadyItems().contains(defendingItemName)) {
                throw new LoggingException(LOGGER,
                        "Melee weapon " + defendingItemName + " is not a ready item for defending combatant " +
                                target.getLabel() + ".");
            }

            Shield shield = utils.getShield(defendingItemName, target.getGameChar().getShields());
            if (shield == null) {
                throw new LoggingException(LOGGER,
                        "Unexpected error. shield " + defendingItemName + " not found in inventory of combatant " +
                                target.getLabel() + ".");
            }
        }
    }

    public void updateTarget(Combatant target, DefenseType defenseType, String defendingItemName) {
        // Update target.
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(defenseType);
        combatDefense.setDefendingItemName(defendingItemName);
        target.getCombatDefenses().add(combatDefense);
    }

    public NextStep resolve(int round, int index, Combatant target) {
        int size = target.getCombatDefenses().size();
        CombatDefense combatDefense = target.getCombatDefenses().get(size - 1);
        DefenseType defenseType = combatDefense.getDefenseType();
        String defendingItemName = combatDefense.getDefendingItemName();

        String defenseDescription = "dodge";
        if (defenseType == DefenseType.PARRY) {
            defenseDescription = "parry with his/her " + defendingItemName;
        } else if (defenseType == DefenseType.BLOCK) {
            defenseDescription = "block with his/her " + defendingItemName;
        }

        String message = target.getLabel() + " has chosen to " + defenseDescription + ".";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.PROMPT_FOR_TO_DEFEND);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }
}
