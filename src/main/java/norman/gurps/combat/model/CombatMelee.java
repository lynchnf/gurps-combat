package norman.gurps.combat.model;

public class CombatMelee implements CombatAttack {
    private String targetLabel;
    private String weaponName;
    private String modeName;
    private Integer toHitEffectiveSkill;
    private Integer toHitRoll;
    private ResultType toHitResultType;
    private Integer damageDice;
    private Integer damageAdds;
    private DamageType damageType;
    private Integer forDamageRoll;
    private Integer targetDamageResistance;
    private Integer penetratingDamage;
    private Integer injuryDamage;

    @Override
    public String getTargetLabel() {
        return targetLabel;
    }

    @Override
    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }

    @Override
    public String getWeaponName() {
        return weaponName;
    }

    @Override
    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    @Override
    public Integer getToHitEffectiveSkill() {
        return toHitEffectiveSkill;
    }

    @Override
    public void setToHitEffectiveSkill(Integer toHitEffectiveSkill) {
        this.toHitEffectiveSkill = toHitEffectiveSkill;
    }

    @Override
    public Integer getToHitRoll() {
        return toHitRoll;
    }

    @Override
    public void setToHitRoll(Integer toHitRoll) {
        this.toHitRoll = toHitRoll;
    }

    @Override
    public ResultType getToHitResultType() {
        return toHitResultType;
    }

    @Override
    public void setToHitResultType(ResultType toHitResultType) {
        this.toHitResultType = toHitResultType;
    }

    @Override
    public Integer getDamageDice() {
        return damageDice;
    }

    @Override
    public void setDamageDice(Integer damageDice) {
        this.damageDice = damageDice;
    }

    @Override
    public Integer getDamageAdds() {
        return damageAdds;
    }

    @Override
    public void setDamageAdds(Integer damageAdds) {
        this.damageAdds = damageAdds;
    }

    @Override
    public DamageType getDamageType() {
        return damageType;
    }

    @Override
    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    @Override
    public Integer getForDamageRoll() {
        return forDamageRoll;
    }

    @Override
    public void setForDamageRoll(Integer forDamageRoll) {
        this.forDamageRoll = forDamageRoll;
    }

    @Override
    public Integer getTargetDamageResistance() {
        return targetDamageResistance;
    }

    @Override
    public void setTargetDamageResistance(Integer targetDamageResistance) {
        this.targetDamageResistance = targetDamageResistance;
    }

    @Override
    public Integer getPenetratingDamage() {
        return penetratingDamage;
    }

    @Override
    public void setPenetratingDamage(Integer penetratingDamage) {
        this.penetratingDamage = penetratingDamage;
    }

    @Override
    public Integer getInjuryDamage() {
        return injuryDamage;
    }

    @Override
    public void setInjuryDamage(Integer injuryDamage) {
        this.injuryDamage = injuryDamage;
    }
}
