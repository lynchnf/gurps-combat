package norman.gurps.model.gamechar;

public class CharWeapon {
    private String weaponName;
    private String skillName;
    private Integer skillLevel;
    private Boolean favorite = false;

    public String getWeaponName() {
        return weaponName;
    }

    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Integer skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return weaponName + " - " + skillName + " (" + skillLevel + ")" + (favorite ? " favorite!" : "");
    }
}
