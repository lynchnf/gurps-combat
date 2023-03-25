package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class MeleeWeapon {
    private String name;
    private Integer skill;
    private List<MeleeWeaponMode> meleeWeaponModes = new ArrayList<>();
    private ParryType parryType;
    private Integer parryModifier;
    private Integer minStrength;
    private Boolean twoHanded;

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

    public List<MeleeWeaponMode> getMeleeWeaponModes() {
        return meleeWeaponModes;
    }

    public void setMeleeWeaponModes(List<MeleeWeaponMode> meleeWeaponModes) {
        this.meleeWeaponModes = meleeWeaponModes;
    }

    public ParryType getParryType() {
        return parryType;
    }

    public void setParryType(ParryType parryType) {
        this.parryType = parryType;
    }

    public Integer getParryModifier() {
        return parryModifier;
    }

    public void setParryModifier(Integer parryModifier) {
        this.parryModifier = parryModifier;
    }

    public Integer getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(Integer minStrength) {
        this.minStrength = minStrength;
    }

    public Boolean getTwoHanded() {
        return twoHanded;
    }

    public void setTwoHanded(Boolean twoHanded) {
        this.twoHanded = twoHanded;
    }
}
