package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.CombatRanged;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.NextStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatAimTargetComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatAimTargetComponent.class);
    private CombatUtils utils;

    public CombatAimTargetComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep prompt(int round, int index, Combatant attacker) {
        String message = attacker.getLabel() + ", please chose a target and a ranged weapon.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_AIM_TARGET);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(String targetLabel, String weaponName, Combatant attacker, List<Combatant> combatants) {
        // Validate target.
        if (targetLabel == null) {
            throw new LoggingException(LOGGER, "Target may not be blank.");
        } else if (targetLabel.equals(attacker.getLabel())) {
            throw new LoggingException(LOGGER, "Combatant " + attacker.getLabel() + " may not target him/herself.");
        } else if (utils.getCombatant(targetLabel, combatants) == null) {
            throw new LoggingException(LOGGER, "Target " + targetLabel + " is not a combatant in the current battle.");
        } else if (!(attacker.getCombatRanged() == null) &&
                !targetLabel.equals(attacker.getCombatRanged().getTargetLabel())) {
            throw new LoggingException(LOGGER, "Target " + targetLabel + " does not match the target (" +
                    attacker.getCombatRanged().getTargetLabel() + ") of the previous aim action.");
        }

        // Validate weapon.
        if (weaponName == null) {
            throw new LoggingException(LOGGER, "Weapon may not be blank.");
        } else if (!attacker.getReadyItems().contains(weaponName)) {
            throw new LoggingException(LOGGER,
                    "Weapon " + weaponName + " is not a ready item for combatant " + attacker.getLabel() + ".");
        } else if (utils.getRangedWeapon(weaponName, attacker.getGameChar().getRangedWeapons()) == null) {
            throw new LoggingException(LOGGER,
                    "Weapon " + weaponName + " is not a valid ranged weapon for combatant " + attacker.getLabel() +
                            ".");
        } else if (!(attacker.getCombatRanged() == null) &&
                !weaponName.equals(attacker.getCombatRanged().getWeaponName())) {
            throw new LoggingException(LOGGER, "Weapon " + weaponName + " does not match the weapon (" +
                    attacker.getCombatRanged().getWeaponName() + ") used in the previous aim action.");
        }
    }

    public void updateAttacker(Combatant attacker, String targetLabel, String weaponName) {
        CombatRanged combatRanged = attacker.getCombatRanged();
        if (combatRanged != null && targetLabel.equals(combatRanged.getTargetLabel()) &&
                weaponName.equals(combatRanged.getWeaponName())) {
            int nbrRoundsAimed = combatRanged.getNbrRoundsAimed();
            combatRanged.setNbrRoundsAimed(nbrRoundsAimed + 1);
        } else {
            combatRanged = new CombatRanged();
            combatRanged.setTargetLabel(targetLabel);
            combatRanged.setWeaponName(weaponName);
            combatRanged.setNbrRoundsAimed(1);
        }
        attacker.setCombatRanged(combatRanged);
    }

    public NextStep resolve(int round, int index, Combatant attacker) {
        String targetLabel = attacker.getCombatRanged().getTargetLabel();
        String weaponName = attacker.getCombatRanged().getWeaponName();
        int nbrRoundsAimed = attacker.getCombatRanged().getNbrRoundsAimed();
        String message;
        if (nbrRoundsAimed == 1) {
            message = attacker.getLabel() + " is aiming at target " + targetLabel + " with " + weaponName + ".";
        } else {
            message = attacker.getLabel() + " continues to aim at target " + targetLabel + " with " + weaponName +
                    ". He/she has now aimed for " + nbrRoundsAimed + " rounds.";
        }

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.END_TURN);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }
}
