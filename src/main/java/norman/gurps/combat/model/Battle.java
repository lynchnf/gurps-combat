package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Battle {
    private Map<String, Combatant> combatants = new HashMap<>();
    private NextStep nextStep;
    private List<BattleLog> logs = new ArrayList<>();

    public Map<String, Combatant> getCombatants() {
        return combatants;
    }

    public void setCombatants(Map<String, Combatant> combatants) {
        this.combatants = combatants;
    }

    public NextStep getNextStep() {
        return nextStep;
    }

    public void setNextStep(NextStep nextStep) {
        this.nextStep = nextStep;
    }

    public List<BattleLog> getLogs() {
        return logs;
    }

    public void setLogs(List<BattleLog> logs) {
        this.logs = logs;
    }
}
