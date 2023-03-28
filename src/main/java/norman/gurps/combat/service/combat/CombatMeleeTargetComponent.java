package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.NextStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatMeleeTargetComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatMeleeTargetComponent.class);
    private CombatUtils utils;

    public CombatMeleeTargetComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep prompt(int round, int index, Combatant attacker) {
        String message = attacker.getLabel() + ", please chose a target, a melee weapon, and a weapon mode.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_MELEE_TARGET);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(String targetLabel, String weaponName, String weaponModeName, Combatant attacker,
            List<Combatant> combatants) {
        // Validate target.
        if (targetLabel == null) {
            throw new LoggingException(LOGGER, "Target may not be blank.");
        } else if (targetLabel.equals(attacker.getLabel())) {
            throw new LoggingException(LOGGER, "Combatant " + attacker.getLabel() + " may not target him/herself.");
        } else if (utils.getCombatant(targetLabel, combatants) == null) {
            throw new LoggingException(LOGGER, "Target " + targetLabel + " is not a combatant in the current battle.");
        } else if (!attacker.getCombatMelees().isEmpty() &&
                !targetLabel.equals(attacker.getCombatMelees().get(0).getTargetLabel())) {
            throw new LoggingException(LOGGER, "Combatant " + attacker.getLabel() +
                    " must attack the same target with both attacks of a double all-out attack. Target " + targetLabel +
                    " does not match the first target " + attacker.getCombatMelees().get(0).getTargetLabel() + ".");
        }

        // Validate weapon/mode.
        if (weaponName == null) {
            throw new LoggingException(LOGGER, "Weapon may not be blank.");
        } else if (!attacker.getReadyItems().contains(weaponName)) {
            throw new LoggingException(LOGGER,
                    "Weapon " + weaponName + " is not a ready item for combatant " + attacker.getLabel() + ".");
        } else {
            MeleeWeapon weapon = utils.getMeleeWeapon(weaponName, attacker.getGameChar().getMeleeWeapons());
            if (weapon == null) {
                throw new LoggingException(LOGGER,
                        "Unexpected error. Melee weapon " + weaponName + " not found in inventory of combatant " +
                                attacker.getLabel() + ".");
            }

            if (weaponModeName == null) {
                throw new LoggingException(LOGGER, "Weapon mode may not be blank.");
            } else if (utils.getMeleeWeaponMode(weaponModeName, weapon.getMeleeWeaponModes()) == null) {
                throw new LoggingException(LOGGER,
                        "Mode " + weaponModeName + " is not a valid mode of melee weapon " + weapon.getName() +
                                " for combatant " + attacker.getLabel() + ".");
            }
        }
    }

    public void updateAttacker(Combatant attacker, String targetLabel, String weaponName, String weaponModeName) {
        CombatMelee combatMelee = new CombatMelee();
        combatMelee.setTargetLabel(targetLabel);
        combatMelee.setWeaponName(weaponName);
        combatMelee.setWeaponModeName(weaponModeName);
        attacker.getCombatMelees().add(combatMelee);
    }

    public NextStep resolve(int round, int index, Combatant attacker) {
        int size = attacker.getCombatMelees().size();
        CombatMelee combatMelee = attacker.getCombatMelees().get(size - 1);
        String targetLabel = combatMelee.getTargetLabel();
        String weaponName = combatMelee.getWeaponName();
        String weaponModeName = combatMelee.getWeaponModeName();
        String message =
                attacker.getLabel() + " is attacking " + targetLabel + " with " + weaponName + "/" + weaponModeName +
                        ".";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.PROMPT_FOR_TO_HIT);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }
}