package norman.gurps.combat.controller.request;

import norman.gurps.combat.model.GameChar;
import org.apache.commons.lang3.StringUtils;

public class StoreCharRequest {
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;

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

    public GameChar toGameChar() {
        GameChar gameChar = new GameChar();
        gameChar.setName(StringUtils.trimToNull(name));
        gameChar.setStrength(strength);
        gameChar.setDexterity(dexterity);
        gameChar.setIntelligence(intelligence);
        gameChar.setHealth(health);
        return gameChar;
    }
}
