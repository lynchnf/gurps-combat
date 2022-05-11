package norman.gurps.model.gamechar;

import java.util.ArrayList;
import java.util.List;

public class GameChar {
    private Long id;
    private String name;
    private Integer strength = 10;
    private Integer dexterity = 10;
    private Integer intelligence = 10;
    private Integer health = 10;
    private Integer hitPointsAdj = 0;
    private Double basicSpeedAdj = 0.0;
    private Integer damageResistance = 0;
    private String shieldName;
    private Integer shieldSkillLevel = 0;
    private List<CharWeapon> charWeapons = new ArrayList<>();
    private Double weightCarried = 0.0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getHitPoints() {
        return strength + hitPointsAdj;
    }

    public void setHitPoints(Integer hitPoints) {
        hitPointsAdj = hitPoints - strength;
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

    public String getShieldName() {
        return shieldName;
    }

    public void setShieldName(String shieldName) {
        this.shieldName = shieldName;
    }

    public Integer getShieldSkillLevel() {
        return shieldSkillLevel;
    }

    public void setShieldSkillLevel(Integer shieldSkillLevel) {
        this.shieldSkillLevel = shieldSkillLevel;
    }

    public List<CharWeapon> getCharWeapons() {
        return charWeapons;
    }

    public void setCharWeapons(List<CharWeapon> charWeapons) {
        this.charWeapons = charWeapons;
    }

    public Double getWeightCarried() {
        return weightCarried;
    }

    public void setWeightCarried(Double weightCarried) {
        this.weightCarried = weightCarried;
    }

    @Override
    public String toString() {
        return name;
    }
}
