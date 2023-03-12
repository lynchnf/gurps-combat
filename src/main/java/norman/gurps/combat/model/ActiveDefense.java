package norman.gurps.combat.model;

public class ActiveDefense {
    private DefenseType defenseType;
    private String defendingItemName;
    private Integer effectiveSkillToDefend;
    private Integer rollToDefend;
    private SkillRollResult toDefendResult;

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

    public Integer getEffectiveSkillToDefend() {
        return effectiveSkillToDefend;
    }

    public void setEffectiveSkillToDefend(Integer effectiveSkillToDefend) {
        this.effectiveSkillToDefend = effectiveSkillToDefend;
    }

    public Integer getRollToDefend() {
        return rollToDefend;
    }

    public void setRollToDefend(Integer rollToDefend) {
        this.rollToDefend = rollToDefend;
    }

    public SkillRollResult getToDefendResult() {
        return toDefendResult;
    }

    public void setToDefendResult(SkillRollResult toDefendResult) {
        this.toDefendResult = toDefendResult;
    }
}