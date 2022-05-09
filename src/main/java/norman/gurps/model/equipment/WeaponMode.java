package norman.gurps.model.equipment;

public class WeaponMode {
    private String modeName;
    private Boolean twoHanded;
    private AttackType attackType;
    private Integer damageDice;
    private Integer damageAdds;
    private DamageType damageType;

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public Boolean getTwoHanded() {
        return twoHanded;
    }

    public void setTwoHanded(Boolean twoHanded) {
        this.twoHanded = twoHanded;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
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
}
