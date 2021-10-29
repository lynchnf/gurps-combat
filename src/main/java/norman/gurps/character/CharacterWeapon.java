package norman.gurps.character;

import norman.gurps.equipment.DamageBase;
import norman.gurps.equipment.DamageType;
import norman.gurps.equipment.Weapon;
import norman.gurps.equipment.WeaponMode;
import norman.gurps.equipment.WeaponSkill;
import norman.gurps.util.MiscUtil;

/**
 * Read-only bean that contains weapons plus additional properties specific to a character.
 */
public class CharacterWeapon {
    private GameCharacter character;
    private String label;
    private Weapon weapon;
    private boolean primary = false;
    private String primarySkillName;
    private String primaryModeName;

    public int getAttack(String skillName) {
        int level = character.getSkill(skillName).getLevel();
        int minimumStrength = weapon.getSkills().get(skillName).getMinimumStrength();
        if (character.getStrength() < minimumStrength) {
            return level + character.getStrength() - minimumStrength;
        } else {
            return level;
        }
    }

    public int getParry(String skillName) {
        int level = getAttack(skillName);
        int defenseBonus = character.getShieldDefenseBonus();
        return level / 2 + 3 + defenseBonus;
    }

    public int getDamageDice(String skillName, String modeName) {
        WeaponSkill skill = weapon.getSkills().get(skillName);
        int minimumStrength = skill.getMinimumStrength();
        int effectiveStrength = Math.min(character.getStrength(), minimumStrength * 3);
        WeaponMode mode = skill.getModes().get(modeName);
        int damageDice = mode.getDamageDice();
        DamageBase damageBase = mode.getDamageBase();
        if (damageBase == DamageBase.SWING) {
            damageDice += MiscUtil.getSwingDamageDice(effectiveStrength);
        } else if (damageBase == DamageBase.THRUST) {
            damageDice += MiscUtil.getThrustDamageDice(effectiveStrength);
        }
        return damageDice;
    }

    public int getDamageAdds(String skillName, String modeName) {
        WeaponSkill skill = weapon.getSkills().get(skillName);
        int minimumStrength = skill.getMinimumStrength();
        int effectiveStrength = Math.min(character.getStrength(), minimumStrength * 3);
        WeaponMode mode = skill.getModes().get(modeName);
        int damageAdds = mode.getDamageAdds();
        DamageBase damageBase = mode.getDamageBase();
        if (damageBase == DamageBase.SWING) {
            damageAdds += MiscUtil.getSwingDamageAdds(effectiveStrength);
        } else if (damageBase == DamageBase.THRUST) {
            damageAdds += MiscUtil.getThrustDamageAdds(effectiveStrength);
        }
        return damageAdds;
    }

    public DamageType getDamageType(String skillName, String modeName) {
        return weapon.getSkills().get(skillName).getModes().get(modeName).getDamageType();
    }

    public String getPrimarySkillName() {
        if (primarySkillName != null) {
            return primarySkillName;
        } else {
            String bestSkillName = null;
            int bestSkillLevel = -1;
            for (WeaponSkill skill : weapon.getSkills().values()) {
                String skillName = skill.getSkillName();
                int level = character.getSkill(skillName).getLevel();
                if (level > bestSkillLevel) {
                    bestSkillName = skillName;
                    bestSkillLevel = level;
                }
            }
            return bestSkillName;
        }
    }

    public String getPrimaryModeName() {
        if (primaryModeName != null) {
            return primaryModeName;
        } else {
            String primarySkillName = getPrimarySkillName();
            String bestModeName = null;
            double bestAverageDamage = -1.0;

            for (WeaponMode mode : weapon.getSkills().get(primarySkillName).getModes().values()) {
                String modeName = mode.getModeName();
                int damageDice = getDamageDice(primarySkillName, modeName);
                int damageAdds = getDamageAdds(primarySkillName, modeName);
                double averageDamage = damageDice * 3.5 + damageAdds;
                if (averageDamage > bestAverageDamage) {
                    bestModeName = modeName;
                    bestAverageDamage = averageDamage;
                }
            }
            return bestModeName;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Constructors, Getters, and Setters /////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CharacterWeapon(GameCharacter character, String label, Weapon weapon) {
        this(character, label, weapon, false, null, null);
    }

    public CharacterWeapon(GameCharacter character, String label, Weapon weapon, boolean primary) {
        this(character, label, weapon, primary, null, null);
    }

    public CharacterWeapon(GameCharacter character, String label, Weapon weapon, String primarySkillName,
            String primaryModeName) {
        this(character, label, weapon, false, primarySkillName, primaryModeName);
    }

    public CharacterWeapon(GameCharacter character, String label, Weapon weapon, boolean primary,
            String primarySkillName, String primaryModeName) {
        this.character = character;
        this.label = label;
        this.weapon = weapon;
        this.primary = primary;
        this.primarySkillName = primarySkillName;
        this.primaryModeName = primaryModeName;
    }

    public String getLabel() {
        return label;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean isPrimary() {
        return primary;
    }
}
