package norman.gurps.combat.model;

import java.util.SortedSet;
import java.util.TreeSet;

public class MeleeWeaponMode {
    private String name;
    private Integer damageDice;
    private Integer damageAdds;
    private DamageType damageType;
    private SortedSet<Integer> reaches = new TreeSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SortedSet<Integer> getReaches() {
        return reaches;
    }

    public void setReaches(SortedSet<Integer> reaches) {
        this.reaches = reaches;
    }
}
