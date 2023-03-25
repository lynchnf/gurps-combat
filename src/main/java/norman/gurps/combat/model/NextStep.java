package norman.gurps.combat.model;

public class NextStep {
    private Integer round;
    private Integer index;
    private CombatPhase combatPhase;
    private Boolean inputNeeded;
    private String message;

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public CombatPhase getCombatPhase() {
        return combatPhase;
    }

    public void setCombatPhase(CombatPhase combatPhase) {
        this.combatPhase = combatPhase;
    }

    public Boolean getInputNeeded() {
        return inputNeeded;
    }

    public void setInputNeeded(Boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
