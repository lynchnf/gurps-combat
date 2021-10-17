package norman.gurps.combat;

public class Action {
    private Maneuver maneuver;
    private String weaponLabel;
    private String skillName;
    private String modeName;

    public Maneuver getManeuver() {
        return maneuver;
    }

    public void setManeuver(Maneuver maneuver) {
        this.maneuver = maneuver;
    }

    public String getWeaponLabel() {
        return weaponLabel;
    }

    public void setWeaponLabel(String weaponLabel) {
        this.weaponLabel = weaponLabel;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }
}
