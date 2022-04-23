package norman.gurps.model;

import org.apache.commons.lang3.StringUtils;

public class GameChar {
    private Long id;
    private String name;
    private Integer strength = 10;
    private Integer dexterity = 10;
    private Integer intelligence = 10;
    private Integer health = 10;
    // Uncomment these later.
    //private Integer damageThrustDice;
    //private Integer damageThrustAdds;
    //private Integer damageSwingDice;
    //private Integer damageSwingAdds;
    //private Float basicLift;
    //private Integer hitPoints;
    //private Integer hitPointsAdj;
    //private Integer will;
    //private Integer willAdj;
    //private Integer perception;
    //private Integer perceptionAdj;
    //private Integer fatigueHits;
    //private Integer fatigueHitsAdj;
    //private Float basicSpeed;
    //private Float basicSpeedAdj;
    //private Integer basicMove;
    //private Integer basicMoveAdj;

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

    @Override
    public String toString() {
        String idStr = id == null ? "new" : id.toString();
        String nameStr = StringUtils.trimToEmpty(name);
        return "(" + idStr + ") " + nameStr;
    }
}
