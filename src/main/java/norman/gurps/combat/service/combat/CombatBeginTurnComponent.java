package norman.gurps.combat.service.combat;

import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatBeginTurnComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatBeginTurnComponent.class);
    private CombatUtils utils;

    public CombatBeginTurnComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public void updateAttacker(Combatant attacker) {
        // Reset attacker. Properties unconsciousnessCheckFailed, nbrOfDeathChecksNeeded, deathCheckFailed, and
        // combatRanged should not need to be reset since they may persist over multiple rounds.
        int currentDamage = attacker.getCurrentDamage();
        int previousDamage = attacker.getPreviousDamage();
        int totalDamage = currentDamage + previousDamage;
        attacker.setCurrentDamage(0);
        attacker.setPreviousDamage(totalDamage);

        int hitPoints = attacker.getGameChar().getHitPoints();
        int remainingHitPoints = hitPoints - totalDamage;
        int hitLevel = utils.getHitLevel(hitPoints, remainingHitPoints);
        boolean unconsciousnessCheckFailed = attacker.getUnconsciousnessCheckFailed();
        boolean deathCheckFailed = attacker.getDeathCheckFailed();
        HealthStatus healthStatus = utils.getHealthStatus(hitLevel, unconsciousnessCheckFailed, deathCheckFailed);
        attacker.setHealthStatus(healthStatus);

        int basicMove = attacker.getGameChar().getBasicMove();
        int encumbranceLevel = attacker.getGameChar().getEncumbranceLevel();
        int currentMove = utils.getCurrentMove(healthStatus, basicMove, encumbranceLevel);
        attacker.setCurrentMove(currentMove);

        int shockPenalty = utils.getShockPenalty(currentDamage);
        attacker.setShockPenalty(shockPenalty);

        attacker.setActionType(null);
        attacker.getCombatMelees().clear();
        attacker.setCombatRanged(null);
        attacker.getCombatDefenses().clear();
    }

    public NextStep resolve(int round, int index, Combatant attacker) {
        int currentDamage = attacker.getCurrentDamage();
        int previousDamage = attacker.getPreviousDamage();
        int totalDamage = currentDamage + previousDamage;
        int hitPoints = attacker.getGameChar().getHitPoints();
        int remainingHitPoints = hitPoints - totalDamage;
        HealthStatus healthStatus = attacker.getHealthStatus();
        int shockPenalty = attacker.getShockPenalty();
        int currentMove = attacker.getCurrentMove();
        int defenseBonus = utils.getDefenseBonus(attacker.getReadyItems(), attacker.getGameChar().getShields());
        int dodge = currentMove + 3 + defenseBonus;

        CombatPhase combatPhase;
        String message;
        if (healthStatus == HealthStatus.ALIVE) {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message = "It is now " + attacker.getLabel() + "'s turn.";
            message += " He/she is at " + remainingHitPoints + " hit points (out of a possible " + hitPoints + ").";
            message += " He/she is " + healthStatus + ".";
            if (shockPenalty < 0) {
                message += " He/she is temporarily at " + shockPenalty +
                        " to all DX and IQ based skills because of shock.";
            }
        } else if (healthStatus == HealthStatus.REELING) {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message = "It is now " + attacker.getLabel() + "'s turn.";
            message += " He/she is at " + remainingHitPoints + " hit points (out of a possible " + hitPoints + ").";
            message += " He/she is " + healthStatus + ".";
            message += " His/her current move is reduced to " + currentMove + " and dodge is reduced to " + dodge + ".";
            if (shockPenalty < 0) {
                message += " He/she is temporarily at " + shockPenalty +
                        " to all DX and IQ based skills because of shock.";
            }
        } else if (healthStatus == HealthStatus.BARELY || healthStatus == HealthStatus.ALMOST ||
                healthStatus == HealthStatus.ALMOST2 || healthStatus == HealthStatus.ALMOST3 ||
                healthStatus == HealthStatus.ALMOST4) {
            combatPhase = CombatPhase.PROMPT_FOR_UNCONSCIOUSNESS_CHECK;
            message = "It is now " + attacker.getLabel() + "'s turn.";
            message += " He/she is at " + remainingHitPoints + " hit points (out of a possible " + hitPoints + ").";
            message += " He/she is " + healthStatus + ".";
            message += " His/her current move is reduced to " + currentMove + " and dodge is reduced to " + dodge + ".";
            if (shockPenalty < 0) {
                message += " He/she is temporarily at " + shockPenalty +
                        " to all DX and IQ based skills because of shock.";
            }
        } else {
            combatPhase = CombatPhase.END_TURN;
            message = attacker.getLabel() + " is at " + remainingHitPoints + " hit points (out of a possible " +
                    hitPoints + ").";
            message += " He/she is " + healthStatus + ".";
            message += " His/her turn is being skipped.";
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
