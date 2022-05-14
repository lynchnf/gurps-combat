package norman.gurps.model.battle;

import norman.gurps.model.equipment.Shield;
import norman.gurps.model.equipment.WeaponMode;
import norman.gurps.model.equipment.WeaponSkill;
import norman.gurps.model.gamechar.CharWeapon;
import norman.gurps.model.gamechar.GameChar;
import norman.gurps.service.MeleeWeaponService;
import norman.gurps.service.ShieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Combatant {
    private static Logger LOGGER = LoggerFactory.getLogger(Combatant.class);
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;
    private Double basicSpeedAdj;
    private Integer damageResistance;
    private Double weightCarried;
    private Integer encumbranceAdj;
    private Integer hitPointsAdj;
    private Integer currentHitPoints;
    private List<CombatantWeapon> combatantWeapons = new ArrayList<>();
    private Integer readyWeaponIndex = -1;
    private CombatantShield combatantShield;
    private Boolean shieldReady;
    private BattleAction lastAction;
    private Boolean currentCombatant = false;

    public Combatant(GameChar gameChar, List<String> existingNames) {
        String name = gameChar.getName();
        int nbr = 0;
        while (existingNames.contains(name)) {
            name = gameChar.getName() + " #" + ++nbr;
        }
        this.name = name;
        strength = gameChar.getStrength();
        dexterity = gameChar.getDexterity();
        intelligence = gameChar.getIntelligence();
        health = gameChar.getHealth();
        basicSpeedAdj = gameChar.getBasicSpeed() - (dexterity + health) / 4.0;
        damageResistance = gameChar.getDamageResistance();
        weightCarried = gameChar.getWeightCarried();
        encumbranceAdj = 0;
        hitPointsAdj = gameChar.getHitPoints() - strength;
        currentHitPoints = gameChar.getHitPoints();
        List<CharWeapon> charWeapons = gameChar.getCharWeapons();
        for (CharWeapon charWeapon : charWeapons) {
            String weaponName = charWeapon.getWeaponName();
            String skillName = charWeapon.getSkillName();
            WeaponSkill weaponSkill = MeleeWeaponService.findWeaponSkill(weaponName, skillName);
            List<WeaponMode> weaponModes = weaponSkill.getModes();
            for (WeaponMode weaponMode : weaponModes) {
                CombatantWeapon combatantWeapon = new CombatantWeapon();
                combatantWeapon.setWeaponName(weaponName);
                combatantWeapon.setSkillName(skillName);
                combatantWeapon.setSkillLevel(charWeapon.getSkillLevel());
                combatantWeapon.setMinimumStrength(weaponSkill.getMinimumStrength());
                combatantWeapon.setBecomesUnReadied(weaponSkill.getBecomesUnReadied());
                combatantWeapon.setModeName(weaponMode.getModeName());
                combatantWeapon.setTwoHanded(weaponMode.getTwoHanded());
                combatantWeapon.setAttackType(weaponMode.getAttackType());
                combatantWeapon.setDamageDice(weaponMode.getDamageDice());
                combatantWeapon.setDamageAdds(weaponMode.getDamageAdds());
                combatantWeapon.setDamageType(weaponMode.getDamageType());
                combatantWeapons.add(combatantWeapon);
            }
        }
        readyWeaponIndex = -1;
        combatantShield = new CombatantShield();
        String shieldName = gameChar.getShieldName();
        combatantShield.setShieldName(shieldName);
        combatantShield.setShieldSkillLevel(gameChar.getShieldSkillLevel());
        Shield shield = ShieldService.getShield(shieldName);
        combatantShield.setDefenseBonus(shield.getDefenseBonus());
        shieldReady = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getDexterity() {
        return dexterity;
    }

    public void setDexterity(Integer dexterity) {
        this.dexterity = dexterity;
    }

    public Integer getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(Integer intelligence) {
        this.intelligence = intelligence;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Double getBasicSpeed() {
        return (dexterity + health) / 4.0 + basicSpeedAdj;
    }

    public void setBasicSpeed(Double basicSpeed) {
        basicSpeedAdj = basicSpeed - (dexterity + health) / 4.0;
    }

    public Integer getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(Integer damageResistance) {
        this.damageResistance = damageResistance;
    }

    public Double getWeightCarried() {
        return weightCarried;
    }

    public void setWeightCarried(Double weightCarried) {
        this.weightCarried = weightCarried;
    }

    public Integer getEncumbrance() {
        return encumbrance() + encumbranceAdj;
    }

    public void setEncumbrance(Integer encumbrance) {
        encumbranceAdj = encumbrance - encumbrance();
    }

    public Integer getHitPoints() {
        return strength + hitPointsAdj;
    }

    public void setHitPoints(Integer hitPoints) {
        hitPointsAdj = hitPoints - strength;
    }

    public Integer getCurrentHitPoints() {
        return currentHitPoints;
    }

    public void setCurrentHitPoints(Integer currentHitPoints) {
        this.currentHitPoints = currentHitPoints;
    }

    public List<CombatantWeapon> getCombatantWeapons() {
        return combatantWeapons;
    }

    public void setCombatantWeapons(List<CombatantWeapon> combatantWeapons) {
        this.combatantWeapons = combatantWeapons;
    }

    public Integer getReadyWeaponIndex() {
        return readyWeaponIndex;
    }

    public void setReadyWeaponIndex(Integer readyWeaponIndex) {
        this.readyWeaponIndex = readyWeaponIndex;
    }

    public CombatantShield getCombatantShield() {
        return combatantShield;
    }

    public void setCombatantShield(CombatantShield combatantShield) {
        this.combatantShield = combatantShield;
    }

    public Boolean getShieldReady() {
        return shieldReady;
    }

    public void setShieldReady(Boolean shieldReady) {
        this.shieldReady = shieldReady;
    }

    public BattleAction getLastAction() {
        return lastAction;
    }

    public void setLastAction(BattleAction lastAction) {
        this.lastAction = lastAction;
    }

    public Boolean getCurrentCombatant() {
        return currentCombatant;
    }

    public void setCurrentCombatant(Boolean currentCombatant) {
        this.currentCombatant = currentCombatant;
    }

    private double basicLift() {
        double basicLift = (strength * strength) / 5.0;
        if (basicLift >= 10.0) {
            return Math.round(basicLift);
        } else {
            return basicLift;
        }
    }

    private int encumbrance() {
        double encumbranceRatio = weightCarried / basicLift();
        if (encumbranceRatio <= 1.0) {
            return 0;
        } else if (encumbranceRatio <= 2.0) {
            return 1;
        } else if (encumbranceRatio <= 3.0) {
            return 2;
        } else if (encumbranceRatio <= 6.0) {
            return 3;
        } else if (encumbranceRatio <= 10.0) {
            return 4;
        } else {
            return 5;
        }
    }

    private int damageThrustDice() {
        if (strength <= 0) {
            return 0;
        } else if (strength < 11) {
            return 1;
        } else {
            return (strength - 11) / 8 + 1;
        }
    }

    private int damageThrustAdds() {
        if (strength <= 0) {
            return 0;
        } else if (strength < 11) {
            return (strength - 1) / 2 - 6;
        } else {
            return (strength - 11) / 2 % 4 - 1;
        }
    }

    private int damageSwingDice() {
        if (strength <= 0) {
            return 0;
        } else if (strength < 9) {
            return 1;
        } else {
            return (strength - 9) / 4 + 1;
        }
    }

    private int damageSwingAdds() {
        if (strength <= 0) {
            return 0;
        } else if (strength < 9) {
            return (strength - 1) / 2 - 5;
        } else {
            return (strength - 9) % 4 - 1;
        }
    }
}
