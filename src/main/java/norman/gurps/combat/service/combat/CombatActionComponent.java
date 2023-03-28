package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.NextStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatActionComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatActionComponent.class);

    public NextStep prompt(int round, int index, Combatant attacker) {
        String message = attacker.getLabel() + ", please chose an action.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_ACTION);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(ActionType actionType) {
        // Validate action.
        if (actionType == null) {
            throw new LoggingException(LOGGER, "Action may not be blank.");
        }
    }

    public void updateAttacker(Combatant attacker, ActionType actionType) {
        attacker.setActionType(actionType);
    }

    public NextStep resolve(int round, int index, Combatant attacker) {
        ActionType actionType = attacker.getActionType();

        CombatPhase combatPhase;
        if (actionType == ActionType.ATTACK_MELEE || actionType == ActionType.AOA_MELEE_4_TO_HIT ||
                actionType == ActionType.AOA_MELEE_2_TO_DMG || actionType == ActionType.MOVE_ATTACK_MELEE) {
            combatPhase = CombatPhase.PROMPT_FOR_MELEE_TARGET;
        } else if (actionType == ActionType.AIM) {
            combatPhase = CombatPhase.PROMPT_FOR_AIM_TARGET;
        } else if (actionType == ActionType.ATTACK_RANGED || actionType == ActionType.AOA_RANGED_1_TO_HIT ||
                actionType == ActionType.MOVE_ATTACK_RANGED) {
            combatPhase = CombatPhase.PROMPT_FOR_RANGED_TARGET;
        } else {
            combatPhase = CombatPhase.END_TURN;
        }
        String message = attacker.getLabel() + " has chosen to " + actionType + ".";

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
