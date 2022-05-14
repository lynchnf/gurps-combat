package norman.gurps.model.battle;

import java.util.ArrayList;
import java.util.List;

public class Battle {
    private Boolean started = false;
    private BattleStage stage;
    private List<Combatant> combatants = new ArrayList<>();

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public BattleStage getStage() {
        return stage;
    }

    public void setStage(BattleStage stage) {
        this.stage = stage;
    }

    public List<Combatant> getCombatants() {
        return combatants;
    }

    public void setCombatants(List<Combatant> combatants) {
        this.combatants = combatants;
    }
}
