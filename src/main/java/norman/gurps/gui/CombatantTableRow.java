package norman.gurps.gui;

import norman.gurps.model.Combatant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatantTableRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatantTableRow.class);
    private final Combatant combatant;
    private ButtonDescriptor buttonDescriptor;

    public CombatantTableRow(Combatant combatant, ButtonDescriptor buttonDescriptor) {
        this.combatant = combatant;
        this.buttonDescriptor = buttonDescriptor;
    }

    public ButtonDescriptor getButtonDescriptor() {
        return buttonDescriptor;
    }

    public void setButtonDescriptor(ButtonDescriptor buttonDescriptor) {
        this.buttonDescriptor = buttonDescriptor;
    }

    public String getName() {
        return combatant.getName();
    }

    public void setName(String name) {
        combatant.setName(name);
    }

    public Integer getStrength() {
        return combatant.getStrength();
    }

    public void setStrength(Integer strength) {
        combatant.setStrength(strength);
    }

    public Integer getDexterity() {
        return combatant.getDexterity();
    }

    public void setDexterity(Integer dexterity) {
        combatant.setDexterity(dexterity);
    }

    public Integer getIntelligence() {
        return combatant.getIntelligence();
    }

    public void setIntelligence(Integer intelligence) {
        combatant.setIntelligence(intelligence);
    }

    public Integer getHealth() {
        return combatant.getHealth();
    }

    public void setHealth(Integer health) {
        combatant.setHealth(health);
    }

    public Integer getHitPoints() {
        return combatant.getHitPoints();
    }

    public void setHitPoints(Integer hitPoints) {
        combatant.setHitPoints(hitPoints);
    }

    public Double getBasicSpeed() {
        return combatant.getBasicSpeed();
    }

    public void setBasicSpeed(Double basicSpeed) {
        combatant.setBasicSpeed(basicSpeed);
    }

    public Integer getDamageResistance() {
        return combatant.getDamageResistance();
    }

    public void setDamageResistance(Integer damageResistance) {
        combatant.setDamageResistance(damageResistance);
    }
}
