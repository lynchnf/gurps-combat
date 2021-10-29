package norman.gurps.equipment;

/**
 * Bean that contains shields as listed in GURPS Lite, page 19.
 */
public class Shield extends Item {
    private String skillName;
    private int defenseBonus;

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
}
