package norman.gurps.equipment;

public class Shield extends Item {
    private String skillName;
    private int defenseBonus;
    private int attackWhileHoldingAdjustment;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public int getAttackWhileHoldingAdjustment() {
        return attackWhileHoldingAdjustment;
    }

    public void setAttackWhileHoldingAdjustment(int attackWhileHoldingAdjustment) {
        this.attackWhileHoldingAdjustment = attackWhileHoldingAdjustment;
    }
}
