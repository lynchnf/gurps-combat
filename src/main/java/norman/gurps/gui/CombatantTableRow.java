package norman.gurps.gui;

import norman.gurps.model.BattleAction;
import norman.gurps.model.Combatant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatantTableRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatantTableRow.class);
    private String name;
    private Integer strength;
    private Integer dexterity;
    private Integer intelligence;
    private Integer health;
    private Double basicSpeed;
    private BattleAction action;

    public CombatantTableRow(Combatant combatant) {
        name = combatant.getName();
        strength = combatant.getStrength();
        dexterity = combatant.getDexterity();
        intelligence = combatant.getIntelligence();
        health = combatant.getHealth();
        basicSpeed = combatant.getBasicSpeed();
        action = combatant.getAction();
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

    public BattleAction getAction() {
        return action;
    }

    public void setAction(BattleAction action) {
        this.action = action;
    }
}
