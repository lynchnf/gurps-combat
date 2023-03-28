package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatDeathCheckComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatDeathCheckComponent.class);
    private CombatUtils utils;

    public CombatDeathCheckComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep prompt(int round, int index, Combatant target) {
        int deathCheck = target.getGameChar().getDeathCheck();
        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();
        String message;
        if (nbrOfDeathChecksNeeded > 1) {
            message = target.getLabel() + " needs to make " + nbrOfDeathChecksNeeded +
                    " rolls to avoid death. Please roll 3d (need " + deathCheck + ").";
        } else {
            message = target.getLabel() + " needs to make a roll to avoid death. Please roll 3d (need " + deathCheck +
                    ").";
        }

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_DEATH_CHECK);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(Integer forDeathCheckRoll) {
        // Validate roll.
        if (forDeathCheckRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled to avoid death may not be blank.");
        } else if (forDeathCheckRoll < 3 || forDeathCheckRoll > 18) {
            throw new LoggingException(LOGGER, "Value rolled to avoid death must be between 3 and 18.");
        }
    }

    public void updateTarget(Combatant target, int forDeathCheckRoll) {
        int deathCheck = target.getGameChar().getDeathCheck();
        ResultType resultType = utils.getResultType(deathCheck, forDeathCheckRoll);
        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();
        boolean deathCheckFailed = false;
        HealthStatus healthStatus = target.getHealthStatus();
        int currentMove = target.getCurrentMove();
        if (resultType == ResultType.CRITICAL_SUCCESS || resultType == ResultType.SUCCESS) {
            nbrOfDeathChecksNeeded--;
        } else {
            nbrOfDeathChecksNeeded = 0;
            deathCheckFailed = true;
            healthStatus = HealthStatus.DEAD;
            currentMove = 0;
        }

        // Update target.
        target.setNbrOfDeathChecksNeeded(nbrOfDeathChecksNeeded);
        target.setDeathCheckFailed(deathCheckFailed);
        target.setHealthStatus(healthStatus);
        target.setCurrentMove(currentMove);
    }

    public NextStep resolve(int round, int index, Combatant target, int forDeathCheckRoll) {
        int deathCheck = target.getGameChar().getDeathCheck();
        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();
        HealthStatus healthStatus = target.getHealthStatus();

        CombatPhase combatPhase = CombatPhase.END_TURN;
        if (healthStatus != HealthStatus.DEAD && nbrOfDeathChecksNeeded > 0) {
            combatPhase = CombatPhase.PROMPT_FOR_DEATH_CHECK;
        }

        String message;
        if (healthStatus == HealthStatus.DEAD) {
            message = target.getLabel() + " is now dead! Rolled a " + forDeathCheckRoll + ", needed a " + deathCheck +
                    ".";
        } else {
            message = target.getLabel() + " successfully avoided death! Rolled a " + forDeathCheckRoll + ", needed a " +
                    deathCheck + ".";
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
