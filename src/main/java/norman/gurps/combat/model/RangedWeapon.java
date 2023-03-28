package norman.gurps.combat.model;

public class RangedWeapon {
    private String name;
    private Integer skill;
    private Integer damageDice;
    private Integer damageAdds;
    private DamageType damageType;
    private Integer accuracy;
    private Integer halfDamageRange;
    private Integer maximumRange;
    private Integer rateOfFire;
    private Integer minimumStrength;
    private Integer bulk;
    private Integer recoil;

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

    public Integer getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(Integer damageDice) {
        this.damageDice = damageDice;
    }

    public Integer getDamageAdds() {
        return damageAdds;
    }

    public void setDamageAdds(Integer damageAdds) {
        this.damageAdds = damageAdds;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getHalfDamageRange() {
        return halfDamageRange;
    }

    public void setHalfDamageRange(Integer halfDamageRange) {
        this.halfDamageRange = halfDamageRange;
    }

    public Integer getMaximumRange() {
        return maximumRange;
    }

    public void setMaximumRange(Integer maximumRange) {
        this.maximumRange = maximumRange;
    }

    public Integer getRateOfFire() {
        return rateOfFire;
    }

    public void setRateOfFire(Integer rateOfFire) {
        this.rateOfFire = rateOfFire;
    }

    public Integer getMinimumStrength() {
        return minimumStrength;
    }

    public void setMinimumStrength(Integer minimumStrength) {
        this.minimumStrength = minimumStrength;
    }

    public Integer getBulk() {
        return bulk;
    }

    public void setBulk(Integer bulk) {
        this.bulk = bulk;
    }

    public Integer getRecoil() {
        return recoil;
    }

    public void setRecoil(Integer recoil) {
        this.recoil = recoil;
    }
}
