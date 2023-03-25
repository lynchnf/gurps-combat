package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private List<Combatant> combatants = new ArrayList<>();
    private NextStep nextStep;
    private List<BattleLog> battleLogs = new ArrayList<>();

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

    public List<BattleLog> getBattleLogs() {
        return battleLogs;
    }

    public void setBattleLogs(List<BattleLog> battleLogs) {
        this.battleLogs = battleLogs;
    }
}
