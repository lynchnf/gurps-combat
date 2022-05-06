package norman.gurps.model;

import java.util.List;

public class Combatant {
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;
    private Double basicSpeed;
    private BattleAction action;

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
        basicSpeed = gameChar.getBasicSpeed();
    }

    public String getName() {
        return name;
    }

    // TODO Remove setter.
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
        return basicSpeed;
    }

    public void setBasicSpeed(Double basicSpeed) {
        this.basicSpeed = basicSpeed;
    }

    public BattleAction getAction() {
        return action;
    }

    public void setAction(BattleAction action) {
        this.action = action;
    }
}