package norman.gurps.skill;

/**
 * Bean that contains skills as listed in GURPS Lite, pages 13-17.
 */
public class Skill {
    private String name;
    private ControllingAttribute controllingAttribute;
    private DifficultyLevel difficultyLevel;

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
}
