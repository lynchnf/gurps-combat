package norman.gurps.combat.controller.request;

import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.Defense;
import norman.gurps.combat.model.Phase;

public class NextStepRequest {
    private Phase phase;
    private Action action;
    private String targetLabel;
    private String weaponName;
    private String modeName;
    private Integer rollToHit;
    private Defense defense;
    private String defendingItemName;
    private Integer rollToDefend;

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public Integer getRollToHit() {
        return rollToHit;
    }

    public void setRollToHit(Integer rollToHit) {
        this.rollToHit = rollToHit;
    }

    public Defense getDefense() {
        return defense;
    }

    public void setDefense(Defense defense) {
        this.defense = defense;
    }

    public String getDefendingItemName() {
        return defendingItemName;
    }

    public void setDefendingItemName(String defendingItemName) {
        this.defendingItemName = defendingItemName;
    }

    public Integer getRollToDefend() {
        return rollToDefend;
    }

    public void setRollToDefend(Integer rollToDefend) {
        this.rollToDefend = rollToDefend;
    }
}