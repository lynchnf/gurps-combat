package norman.gurps.equipment;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean that contains skills associated with weapons as listed in GURPS Lite, pages 20-21.
 */
public class WeaponSkill {
    private String skillName;
    private int minimumStrength;
    private Map<String, WeaponMode> modes = new HashMap<>();

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getMinimumStrength() {
        return minimumStrength;
    }

    public void setMinimumStrength(int minimumStrength) {
        this.minimumStrength = minimumStrength;
    }

    public Map<String, WeaponMode> getModes() {
        return modes;
    }

    public void setModes(Map<String, WeaponMode> modes) {
        this.modes = modes;
    }
}
