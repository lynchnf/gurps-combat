package norman.gurps.model.battle;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private List<Combatant> combatants = new ArrayList<>();
    private List<BattleLog> battleLogs = new ArrayList<>();

    public List<Combatant> getCombatants() {
        return combatants;
    }

    public void setCombatants(List<Combatant> combatants) {
        this.combatants = combatants;
    }

    public List<BattleLog> getBattleLogs() {
        return battleLogs;
    }

    public void setBattleLogs(List<BattleLog> battleLogs) {
        this.battleLogs = battleLogs;
    }

    @Override
    public String toString() {
        return "Battle{" + "combatants=" + combatants + ", battleLogs=" + battleLogs + '}';
    }
}
