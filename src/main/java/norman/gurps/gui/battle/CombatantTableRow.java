package norman.gurps.gui.battle;

import norman.gurps.gui.ButtonDescriptor;
import norman.gurps.model.battle.BattleAction;
import norman.gurps.model.battle.Combatant;
import norman.gurps.model.battle.CombatantShield;
import norman.gurps.model.battle.CombatantWeapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CombatantTableRow {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatantTableRow.class);
    private Combatant combatant;
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

    public Integer getEncumbrance() {
        return combatant.getEncumbrance();
    }

    public void setEncumbrance(Integer encumbrance) {
        combatant.setEncumbrance(encumbrance);
    }

    public Integer getHitPoints() {
        return combatant.getHitPoints();
    }

    public void setHitPoints(Integer hitPoints) {
        combatant.setHitPoints(hitPoints);
    }

    public Integer getCurrentHitPoints() {
        return combatant.getCurrentHitPoints();
    }

    public void setCurrentHitPoints(Integer currentHitPoints) {
        combatant.setCurrentHitPoints(currentHitPoints);
    }

    public List<CombatantWeapon> getCombatantWeapons() {
        return combatant.getCombatantWeapons();
    }

    public void setCombatantWeapons(List<CombatantWeapon> combatantWeapons) {
        combatant.setCombatantWeapons(combatantWeapons);
    }

    public Integer getReadyWeaponIndex() {
        return combatant.getReadyWeaponIndex();
    }

    public void setReadyWeaponIndex(Integer readyWeaponIndex) {
        combatant.setReadyWeaponIndex(readyWeaponIndex);
    }

    public CombatantShield combatantShield() {
        return combatant.getCombatantShield();
    }

    public CombatantShield getReadyShield() {
        if (combatant.getShieldReady()) {
            return combatant.getCombatantShield();
        } else {
            return null;
        }
    }

    public void setReadyShield(CombatantShield readyShield) {
        if (readyShield == null) {
            combatant.setShieldReady(false);
        } else {
            combatant.setShieldReady(true);
        }
    }

    public BattleAction getLastAction() {
        return combatant.getLastAction();
    }

    public void setLastAction(BattleAction lastAction) {
        combatant.setLastAction(lastAction);
    }

    public Boolean getCurrentCombatant() {
        return combatant.getCurrentCombatant();
    }

    public void setCurrentCombatant(Boolean currentCombatant) {
        combatant.setCurrentCombatant(currentCombatant);
    }
}
