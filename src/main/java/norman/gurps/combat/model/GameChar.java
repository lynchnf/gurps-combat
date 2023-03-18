package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class GameChar {
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;
    private Integer hitPoints;
    private Double basicSpeed;
    private Integer basicMove;
    private Integer encumbranceLevel;
    private Integer deathCheck;
    private List<MeleeWeapon> meleeWeapons = new ArrayList<>();
    private List<Shield> shields = new ArrayList<>();
    private List<Armor> armorList = new ArrayList<>();

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

    public Integer getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(Integer hitPoints) {
        this.hitPoints = hitPoints;
    }

    public Double getBasicSpeed() {
        return basicSpeed;
    }

    public void setBasicSpeed(Double basicSpeed) {
        this.basicSpeed = basicSpeed;
    }

    public Integer getBasicMove() {
        return basicMove;
    }

    public void setBasicMove(Integer basicMove) {
        this.basicMove = basicMove;
    }

    public Integer getEncumbranceLevel() {
        return encumbranceLevel;
    }

    public void setEncumbranceLevel(Integer encumbranceLevel) {
        this.encumbranceLevel = encumbranceLevel;
    }

    public Integer getDeathCheck() {
        return deathCheck;
    }

    public void setDeathCheck(Integer deathCheck) {
        this.deathCheck = deathCheck;
    }

    public List<MeleeWeapon> getMeleeWeapons() {
        return meleeWeapons;
    }

    public void setMeleeWeapons(List<MeleeWeapon> meleeWeapons) {
        this.meleeWeapons = meleeWeapons;
    }

    public List<Shield> getShields() {
        return shields;
    }

    public void setShields(List<Shield> shields) {
        this.shields = shields;
    }

    public List<Armor> getArmorList() {
        return armorList;
    }

    public void setArmorList(List<Armor> armorList) {
        this.armorList = armorList;
    }
}