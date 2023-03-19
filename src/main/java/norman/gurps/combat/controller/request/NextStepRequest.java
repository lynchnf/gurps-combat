package norman.gurps.combat.controller.request;

import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.Phase;

public class NextStepRequest {
    private Phase phase;
    private Action action;
    private String targetLabel;
    private String weaponName;
    private String modeName;
    private Integer rollToHit;
    private DefenseType defenseType;
    private String defendingItemName;
    private Integer rollToDefend;
    private Integer rollForDamage;
    private Integer rollForDeathCheck;
    private Integer rollForUnconsciousnessCheck;

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

    public DefenseType getDefenseType() {
        return defenseType;
    }

    public void setDefenseType(DefenseType defenseType) {
        this.defenseType = defenseType;
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

    public Integer getRollForDamage() {
        return rollForDamage;
    }

    public void setRollForDamage(Integer rollForDamage) {
        this.rollForDamage = rollForDamage;
    }

    public Integer getRollForDeathCheck() {
        return rollForDeathCheck;
    }

    public void setRollForDeathCheck(Integer rollForDeathCheck) {
        this.rollForDeathCheck = rollForDeathCheck;
    }

    public Integer getRollForUnconsciousnessCheck() {
        return rollForUnconsciousnessCheck;
    }

    public void setRollForUnconsciousnessCheck(Integer rollForUnconsciousnessCheck) {
        this.rollForUnconsciousnessCheck = rollForUnconsciousnessCheck;
    }
}