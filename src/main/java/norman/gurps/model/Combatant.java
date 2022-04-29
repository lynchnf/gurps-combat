package norman.gurps.model;

import java.util.List;

public class Combatant {
    private GameChar gameChar;
    private String name;

    public Combatant(GameChar gameChar, List<String> existingNames) {
        this.gameChar = gameChar;
        String name = gameChar.getName();
        int nbr = 0;
        while (existingNames.contains(name)) {
            name = gameChar.getName() + " #" + ++nbr;
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStrength() {
        return gameChar.getStrength();
    }

    public Integer getDexterity() {
        return gameChar.getDexterity();
    }

    public Integer getIntelligence() {
        return gameChar.getIntelligence();
    }

    public Integer getHealth() {
        return gameChar.getHealth();
    }

    public Double getBasicSpeed() {
        return gameChar.getBasicSpeed();
    }
}