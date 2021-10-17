package norman.gurps.util;

public class RollResult {
    private int effectiveSkill;
    private int rollValue;
    private RollStatus status;

    public int getEffectiveSkill() {
        return effectiveSkill;
    }

    public void setEffectiveSkill(int effectiveSkill) {
        this.effectiveSkill = effectiveSkill;
    }

    public int getRollValue() {
        return rollValue;
    }

    public void setRollValue(int rollValue) {
        this.rollValue = rollValue;
    }

    public RollStatus getStatus() {
        return status;
    }

    public void setStatus(RollStatus status) {
        this.status = status;
    }
}
