package norman.gurps.character;

import norman.gurps.LoggingException;
import norman.gurps.equipment.Armor;
import norman.gurps.equipment.Item;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameCharacter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCharacter.class);
    private String name;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int health;
    private Map<String, CharacterSkill> skills = new HashMap<>();
    private Map<String, Item> equipment = new HashMap<>();
    private Map<String, Armor> armors = new HashMap<>();
    private Map<String, CharacterShield> shields = new HashMap<>();
    private Map<String, CharacterWeapon> weapons = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public double getBasicLift() {
        double basicLift = strength * strength / 5.0;
        if (basicLift >= 10.0) {
            return Math.round(basicLift);
        } else {
            return basicLift;
        }
    }

    public int getHitPoints() {
        return strength;
    }

    public int getWill() {
        return intelligence;
    }

    public int getPerception() {
        return intelligence;
    }

    public int getFatiguePoints() {
        return health;
    }

    public double getBasicSpeed() {
        return (dexterity + health) / 4.0;
    }

    public int getBasicMove() {
        return (int) getBasicSpeed();
    }

    public int getEncumbranceLevel() {
        double totalWeight = 0;
        for (Item item : equipment.values()) {
            totalWeight += item.getWeight();
        }
        double basicLift = getBasicLift();
        if (totalWeight <= basicLift) {
            return 0;
        } else if (totalWeight <= basicLift * 2) {
            return 1;
        } else if (totalWeight <= basicLift * 3) {
            return 2;
        } else if (totalWeight <= basicLift * 6) {
            return 3;
        } else if (totalWeight <= basicLift * 10) {
            return 4;
        } else {
            return 5;
        }
    }

    public int getMove() {
        int basicMove = getBasicMove();
        int encumbranceLevel = getEncumbranceLevel();
        double multiplier = (5.0 - encumbranceLevel) / basicMove;
        return (int) (basicMove * multiplier);
    }

    public int getThrustDamageDice() {
        return strength < 11 ? 1 : (strength - 3) / 8;
    }

    public int getThrustDamageAdds() {
        return strength < 11 ? (strength - 1) / 2 - 6 : (strength - 3) % 8 / 2 - 1;
    }

    public int getSwingDamageDice() {
        return strength < 9 ? 1 : (strength - 5) / 4;
    }

    public int getSwingDamageAdds() {
        return strength < 9 ? (strength - 1) / 2 - 5 : (strength - 5) % 4 - 1;
    }

    public void addSkill(Skill skill, int points) {
        addSkill(skill, points, skill.getName());
    }

    public void addSkill(Skill skill, int points, String label) {
        if (skills.containsKey(label)) {
            throw new LoggingException(LOGGER, "Character " + name + " already has a skill with label " + label + ".");
        }
        CharacterSkill charSkill = new CharacterSkill(this, label, skill, points);
        skills.put(label, charSkill);
    }

    public CharacterSkill getSkill(String label) {
        return skills.get(label);
    }

    public void addEquipment(Item item) {
        equipment.put(item.getName(), item);
        if (item instanceof Armor) {
            armors.put(item.getName(), (Armor) item);
        } else if (item instanceof Shield) {
            String label = item.getName();
            Shield shield = (Shield) item;
            boolean primary = shields.isEmpty();
            CharacterShield charShield = new CharacterShield(this, label, shield, primary);
            shields.put(label, charShield);
        }
        if (item instanceof Weapon) {
            String label = item.getName();
            Weapon weapon = (Weapon) item;
            boolean primary = weapons.isEmpty();
            CharacterWeapon charWeapon = new CharacterWeapon(this, label, weapon, primary);
            weapons.put(label, charWeapon);
        }
    }

    public Item getItem(String name) {
        return equipment.get(name);
    }

    public Armor getArmor(String label) {
        return armors.get(label);
    }

    public CharacterShield getShield(String label) {
        return shields.get(label);
    }

    public CharacterShield getPrimaryShield() {
        CharacterShield primaryShield = null;
        for (CharacterShield shield : shields.values()) {
            if (shield.isPrimary()) {
                primaryShield = shield;
                break;
            }
        }
        return primaryShield;
    }

    public CharacterWeapon getWeapon(String label) {
        return weapons.get(label);
    }

    public CharacterWeapon getPrimaryWeapon() {
        CharacterWeapon primaryWeapon = null;
        for (CharacterWeapon weapon : weapons.values()) {
            if (weapon.isPrimary()) {
                primaryWeapon = weapon;
                break;
            }
        }
        return primaryWeapon;
    }

    public int getShieldDefenseBonus() {
        int shieldDefenseBonus = 0;
        for (CharacterShield shield : shields.values()) {
            shieldDefenseBonus += shield.getShield().getDefenseBonus();
        }
        return shieldDefenseBonus;
    }

    public int getDodge() {
        double basicSpeed = getBasicSpeed();
        int encumbranceLevel = getEncumbranceLevel();
        int shieldBonus = getShieldDefenseBonus();
        return (int) (basicSpeed + 3 - encumbranceLevel + shieldBonus);
    }

    public int getArmorDamageResistance() {
        int armorDamageResistance = 0;
        for (Armor armor : armors.values()) {
            armorDamageResistance += armor.getDamageResistance();
        }
        return armorDamageResistance;
    }
}
