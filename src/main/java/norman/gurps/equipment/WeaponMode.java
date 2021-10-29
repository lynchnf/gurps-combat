package norman.gurps.equipment;

/**
 * Bean that contains modes of use associated with weapon skills as listed in GURPS Lite, pages 20-21.
 */
public class WeaponMode {
    private String modeName;
    private DamageBase damageBase;
    private int damageDice = 0;
    private int damageAdds = 0;
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
