package norman.gurps.model.equipment;

import java.util.ArrayList;
import java.util.List;

public class WeaponSkill {
    private String skillName;
    private Integer minimumStrength;
    private Boolean becomesUnReadied;
    private List<WeaponMode> modes = new ArrayList<>();

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getMinimumStrength() {
        return minimumStrength;
    }

    public void setMinimumStrength(Integer minimumStrength) {
        this.minimumStrength = minimumStrength;
    }

    public Boolean getBecomesUnReadied() {
        return becomesUnReadied;
    }

    public void setBecomesUnReadied(Boolean becomesUnReadied) {
        this.becomesUnReadied = becomesUnReadied;
    }

    public List<WeaponMode> getModes() {
        return modes;
    }

    public void setModes(List<WeaponMode> modes) {
        this.modes = modes;
    }
}
