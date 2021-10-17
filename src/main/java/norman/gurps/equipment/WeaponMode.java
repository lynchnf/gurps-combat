package norman.gurps.equipment;

public class WeaponMode {
    private String modeName;
    private DamageBase damageBase;
    private int damageDice;
    private int damageAdds;
    private DamageType damageType;

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public DamageBase getDamageBase() {
        return damageBase;
    }

    public void setDamageBase(DamageBase damageBase) {
        this.damageBase = damageBase;
    }

    public int getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(int damageDice) {
        this.damageDice = damageDice;
    }

    public int getDamageAdds() {
        return damageAdds;
    }

    public void setDamageAdds(int damageAdds) {
        this.damageAdds = damageAdds;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }
}
