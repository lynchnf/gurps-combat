package norman.gurps.skill;

public class Skill {
    private String name;
    private ControllingAttribute controllingAttribute;
    private DifficultyLevel difficultyLevel;
    private int parryWithAdjustment = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ControllingAttribute getControllingAttribute() {
        return controllingAttribute;
    }

    public void setControllingAttribute(ControllingAttribute controllingAttribute) {
        this.controllingAttribute = controllingAttribute;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getParryWithAdjustment() {
        return parryWithAdjustment;
    }

    public void setParryWithAdjustment(int parryWithAdjustment) {
        this.parryWithAdjustment = parryWithAdjustment;
    }
}
