package norman.gurps.combat.service.combat;

import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.NextStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatEndTurnComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatEndTurnComponent.class);

    public void updateAttacker(Combatant attacker) {
        attacker.setShockPenalty(0);
    }

    public NextStep resolve(int round, int index, int nbrOfCombatants) {
        // Create next step.
        NextStep nextStep = new NextStep();
        int newRound = round;
        int newIndex = index + 1;
        if (newIndex >= nbrOfCombatants) {
            newRound = round + 1;
            newIndex = 0;
        }
        nextStep.setRound(newRound);
        nextStep.setIndex(newIndex);
        nextStep.setCombatPhase(CombatPhase.BEGIN_TURN);
        nextStep.setInputNeeded(false);
        return nextStep;
    }
}
