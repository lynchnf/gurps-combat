package norman.gurps.combat.model;

import java.util.Set;

public class Combatant {
    private String label;
    private GameChar gameChar;
    private Integer currentDamage;
    private Integer previousDamage;

    public Combatant() {
    }

    public Combatant(GameChar gameChar, Set<String> existingLabels) {
        String label = gameChar.getName();
        int i = 2;
        while (existingLabels.contains(label)) {
            label = gameChar.getName() + " " + i++;
        }
        this.label = label;
        this.gameChar = gameChar;
        currentDamage = 0;
        previousDamage = 0;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public GameChar getGameChar() {
        return gameChar;
    }

    public void setGameChar(GameChar gameChar) {
        this.gameChar = gameChar;
    }

    public Integer getCurrentDamage() {
        return currentDamage;
    }

    public void setCurrentDamage(Integer currentDamage) {
        this.currentDamage = currentDamage;
    }

    public Integer getPreviousDamage() {
        return previousDamage;
    }

    public void setPreviousDamage(Integer previousDamage) {
        this.previousDamage = previousDamage;
    }
}
