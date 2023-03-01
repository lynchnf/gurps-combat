package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class MeleeWeapon {
    private String name;
    private Integer skill;
    private List<MeleeWeaponMode> modes = new ArrayList<>();
    private Integer minStrength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSkill() {
        return skill;
    }

    public void setSkill(Integer skill) {
        this.skill = skill;
    }

    public List<MeleeWeaponMode> getModes() {
        return modes;
    }

    public void setModes(List<MeleeWeaponMode> modes) {
        this.modes = modes;
    }

    public Integer getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(Integer minStrength) {
        this.minStrength = minStrength;
    }
}
