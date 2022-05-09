package norman.gurps.model.equipment;

import java.util.ArrayList;
import java.util.List;

public class MeleeWeapon {
    private String name;
    private final List<WeaponSkill> skills = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WeaponSkill> getSkills() {
        return skills;
    }
}
