package foo.bar.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTableModelRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyTableModelRow.class);
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;
    private Double basicSpeed;
    private MyAction action;

    public MyTableModelRow(String name, Integer strength, Integer dexterity, Integer intelligence, Integer health,
            Double basicSpeed) {
        this.name = name;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.health = health;
        this.basicSpeed = basicSpeed;
        this.action = MyAction.BLANK;
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
        return basicSpeed;
    }

    public void setBasicSpeed(Double basicSpeed) {
        this.basicSpeed = basicSpeed;
    }

    public MyAction getAction() {
        return action;
    }

    public void setAction(MyAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "MyTableModelRow{" + "name='" + name + '\'' + '}';
    }
}
