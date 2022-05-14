package norman.gurps.model.battle;

import norman.gurps.model.equipment.AttackType;
import norman.gurps.model.equipment.DamageType;

public class CombatantWeapon {
    private String weaponName;
    private String skillName;
    private Integer skillLevel;
    private Integer minimumStrength;
    private Boolean becomesUnReadied;
    private String modeName;
    private Boolean twoHanded;
    private AttackType attackType;
    private Integer damageDice;
    private Integer damageAdds;
    private DamageType damageType;

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

    public Integer getMinimumStrength() {
        return minimumStrength;
    }

    public void setMinimumStrength(Integer minimumStrength) {
        this.minimumStrength = minimumStrength;
    }

    public Boolean getBecomesUnReadied() {
        return becomesUnReadied;
    }

    public void setBecomesUnReadied(Boolean becomesUnReadied) {
        this.becomesUnReadied = becomesUnReadied;
    }

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

    @Override
    public String toString() {
        StringBuilder damage = null;
        if (attackType != null) {
            damage = new StringBuilder(attackType.getAbbreviation());
        }
        if (damageDice != 0) {
            if (damage == null) {
                damage = new StringBuilder();
            } else {
                damage.append("+");
            }
            damage.append(damageDice);
            damage.append("d");
        }
        if (damageAdds != null) {
            if (damage == null) {
                damage = new StringBuilder();
            } else {
                damage.append("+");
            }
            damage.append(damageAdds);
        }
        return weaponName + " (" + skillLevel + ") " + modeName + ": " + damage + " " + damageType.getAbbreviation();
    }
}
