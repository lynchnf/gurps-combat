package norman.gurps.model.battle;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private boolean started;
    private String currentCombatant;
    private List<Combatant> combatants = new ArrayList<>();

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getCurrentCombatant() {
        return currentCombatant;
    }

    public void setCurrentCombatant(String currentCombatant) {
        this.currentCombatant = currentCombatant;
    }

    public List<Combatant> getCombatants() {
        return combatants;
    }

    public void setCombatants(List<Combatant> combatants) {
        this.combatants = combatants;
    }
}
