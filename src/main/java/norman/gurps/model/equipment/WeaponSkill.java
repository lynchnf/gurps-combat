package norman.gurps.model.equipment;

import java.util.ArrayList;
import java.util.List;

public class WeaponSkill {
    private String skillName;
    private Integer minimumStrength;
    private Boolean unReadied;
    private final List<WeaponMode> modes = new ArrayList<>();

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

    public Boolean getUnReadied() {
        return unReadied;
    }

    public void setUnReadied(Boolean unReadied) {
        this.unReadied = unReadied;
    }

    public List<WeaponMode> getModes() {
        return modes;
    }
}
