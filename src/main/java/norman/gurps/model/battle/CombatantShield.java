package norman.gurps.model.battle;

public class CombatantShield {
    private String shieldName;
    private Integer shieldSkillLevel;
    private Integer defenseBonus;

    public String getShieldName() {
        return shieldName;
    }

    public void setShieldName(String shieldName) {
        this.shieldName = shieldName;
    }

    public Integer getShieldSkillLevel() {
        return shieldSkillLevel;
    }

    public void setShieldSkillLevel(Integer shieldSkillLevel) {
        this.shieldSkillLevel = shieldSkillLevel;
    }

    public Integer getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(Integer defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    @Override
    public String toString() {
        return shieldName + " (" + shieldSkillLevel + ") DB: " + defenseBonus;
    }
}
