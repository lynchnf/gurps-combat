package norman.gurps.model;

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
    private Integer shieldDefenseBonus = 0;
    private final List<MeleeWeapon> meleeWeapons = new ArrayList<>();
    private final List<RangedWeapon> rangedWeapons = new ArrayList<>();
    private final Double weightCarried = 0.0;

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

    //public Integer getDamageThrustDice() {
    //    if (strength <= 0) {
    //        return 0;
    //    } else if (strength < 11) {
    //        return 1;
    //    } else {
    //        return (strength - 11) / 8 + 1;
    //    }
    //}
    //
    //public Integer getDamageThrustAdds() {
    //    if (strength <= 0) {
    //        return 0;
    //    } else if (strength < 11) {
    //        return (strength - 1) / 2 - 6;
    //    } else {
    //        return (strength - 11) / 2 % 4 - 1;
    //    }
    //}
    //
    //public Integer getDamageSwingDice() {
    //    if (strength <= 0) {
    //        return 0;
    //    } else if (strength < 9) {
    //        return 1;
    //    } else {
    //        return (strength - 9) / 4 + 1;
    //    }
    //}
    //
    //public Integer getDamageSwingAdds() {
    //    if (strength <= 0) {
    //        return 0;
    //    } else if (strength < 9) {
    //        return (strength - 1) / 2 - 5;
    //    } else {
    //        return (strength - 9) % 4 - 1;
    //    }
    //}
    //
    //public Double getBasicLift() {
    //    double basicLift = (double) (strength * strength) / 5;
    //    if (basicLift < 10.0) {
    //        return basicLift;
    //    } else {
    //        return (double) Math.round(basicLift);
    //    }
    //}

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

    public Integer getShieldDefenseBonus() {
        return shieldDefenseBonus;
    }

    public void setShieldDefenseBonus(Integer shieldDefenseBonus) {
        this.shieldDefenseBonus = shieldDefenseBonus;
    }

    @Override
    public String toString() {
        return name;
    }
}
