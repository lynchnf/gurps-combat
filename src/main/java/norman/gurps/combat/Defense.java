package norman.gurps.combat;

public class Defense {
    private DefenseType type;
    private String itemLabel;
    private String skillName;

    public DefenseType getType() {
        return type;
    }

    public void setType(DefenseType type) {
        this.type = type;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
