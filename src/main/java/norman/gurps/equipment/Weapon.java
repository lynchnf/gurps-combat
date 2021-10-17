package norman.gurps.equipment;

import java.util.HashMap;
import java.util.Map;

public class Weapon extends Item {
    private Map<String, WeaponSkill> skills = new HashMap<>();

    public Map<String, WeaponSkill> getSkills() {
        return skills;
    }

    public void setSkills(Map<String, WeaponSkill> skills) {
        this.skills = skills;
    }
}
