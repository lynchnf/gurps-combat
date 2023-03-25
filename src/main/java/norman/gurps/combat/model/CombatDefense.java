package norman.gurps.combat.model;

public class CombatDefense {
    private DefenseType defenseType;
    private String defendingItemName;
    private Integer toDefendEffectiveSkill;
    private Integer toDefendRoll;
    private ResultType toDefendResult;

    public DefenseType getDefenseType() {
        return defenseType;
    }

    public void setDefenseType(DefenseType defenseType) {
        this.defenseType = defenseType;
    }

    public String getDefendingItemName() {
        return defendingItemName;
    }

    public void setDefendingItemName(String defendingItemName) {
        this.defendingItemName = defendingItemName;
    }

    public Integer getToDefendEffectiveSkill() {
        return toDefendEffectiveSkill;
    }

    public void setToDefendEffectiveSkill(Integer toDefendEffectiveSkill) {
        this.toDefendEffectiveSkill = toDefendEffectiveSkill;
    }

    public Integer getToDefendRoll() {
        return toDefendRoll;
    }

    public void setToDefendRoll(Integer toDefendRoll) {
        this.toDefendRoll = toDefendRoll;
    }

    public ResultType getToDefendResult() {
        return toDefendResult;
    }

    public void setToDefendResult(ResultType toDefendResult) {
        this.toDefendResult = toDefendResult;
    }
}
