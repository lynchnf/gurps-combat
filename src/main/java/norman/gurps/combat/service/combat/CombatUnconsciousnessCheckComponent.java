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
public class CombatUnconsciousnessCheckComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatUnconsciousnessCheckComponent.class);
    private CombatUtils utils;

    public CombatUnconsciousnessCheckComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep prompt(int round, int index, Combatant attacker) {
        int unconsciousnessCheck = attacker.getGameChar().getUnconsciousnessCheck();
        String message = attacker.getLabel() + " needs to make a roll to stay conscious. Please roll 3d (need " +
                unconsciousnessCheck + ").";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_UNCONSCIOUSNESS_CHECK);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(Integer forUnconsciousnessCheckRoll) {
        // Validate roll.
        if (forUnconsciousnessCheckRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled to remain conscious may not be blank.");
        } else if (forUnconsciousnessCheckRoll < 3 || forUnconsciousnessCheckRoll > 18) {
            throw new LoggingException(LOGGER, "To hit roll must be between 3 and 18.");
        }
    }

    public void updateAttacker(Combatant attacker, int forUnconsciousnessCheckRoll) {
        int unconsciousnessCheck = attacker.getGameChar().getUnconsciousnessCheck();
        ResultType resultType = utils.getResultType(unconsciousnessCheck, forUnconsciousnessCheckRoll);

        boolean unconsciousnessCheckFailed = false;
        HealthStatus healthStatus = attacker.getHealthStatus();
        int currentMove = attacker.getCurrentMove();
        if (resultType == ResultType.CRITICAL_FAILURE || resultType == ResultType.FAILURE) {
            unconsciousnessCheckFailed = true;
            healthStatus = HealthStatus.UNCONSCIOUS;
            currentMove = 0;
        }

        // Update combatant.
        attacker.setUnconsciousnessCheckFailed(unconsciousnessCheckFailed);
        attacker.setHealthStatus(healthStatus);
        attacker.setCurrentMove(currentMove);
    }

    public NextStep resolve(int round, int index, Combatant attacker, int forUnconsciousnessCheckRoll) {
        int unconsciousnessCheck = attacker.getGameChar().getUnconsciousnessCheck();
        CombatPhase combatPhase;
        String message;
        if (attacker.getHealthStatus() == HealthStatus.UNCONSCIOUS) {
            combatPhase = CombatPhase.END_TURN;
            message = attacker.getLabel() + " is now unconscious! Rolled a " + forUnconsciousnessCheckRoll +
                    ", needed a " + unconsciousnessCheck + ".";
        } else {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message =
                    attacker.getLabel() + " successfully remained conscious! Rolled a " + forUnconsciousnessCheckRoll +
                            ", needed a " + unconsciousnessCheck + ".";
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
