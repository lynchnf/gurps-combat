package norman.gurps.model;

import java.util.List;

public class Combatant {
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;
    private Integer hitPointsAdj;
    private Double basicSpeedAdj;
    private Integer damageResistance;

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
        hitPointsAdj = gameChar.getHitPoints() - strength;
        basicSpeedAdj = gameChar.getBasicSpeed() - (dexterity + health) / 4.0;
        damageResistance = gameChar.getDamageResistance();
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

    @Override
    public String toString() {
        return name;
    }
}
