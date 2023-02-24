package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private List<Combatant> combatants = new ArrayList<>();
    private NextStep nextStep;
    private List<String> logs = new ArrayList<>();

    public List<Combatant> getCombatants() {
        return combatants;
    }

    public void setCombatants(List<Combatant> combatants) {
        this.combatants = combatants;
    }

    public NextStep getNextStep() {
        return nextStep;
    }

    public void setNextStep(NextStep nextStep) {
        this.nextStep = nextStep;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
