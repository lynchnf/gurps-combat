package norman.gurps.combat.controller.request;

import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.DefenseType;

public class NextStepRequest {
    private CombatPhase combatPhase;
    private ActionType actionType;
    private String targetLabel;
    private String weaponName;
    private String weaponModeName;
    private Integer speedAndRange;
    private Integer toHitRoll;
    private DefenseType defenseType;
    private String defendingItemName;
    private Integer toDefendRoll;
    private Integer forDamageRoll;
    private Integer forDeathCheckRoll;
    private Integer forUnconsciousnessCheckRoll;

    public CombatPhase getCombatPhase() {
        return combatPhase;
    }

    public void setCombatPhase(CombatPhase combatPhase) {
        this.combatPhase = combatPhase;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
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

    public String getWeaponModeName() {
        return weaponModeName;
    }

    public void setWeaponModeName(String weaponModeName) {
        this.weaponModeName = weaponModeName;
    }

    public Integer getSpeedAndRange() {
        return speedAndRange;
    }

    public void setSpeedAndRange(Integer speedAndRange) {
        this.speedAndRange = speedAndRange;
    }

    public Integer getToHitRoll() {
        return toHitRoll;
    }

    public void setToHitRoll(Integer toHitRoll) {
        this.toHitRoll = toHitRoll;
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

    public Integer getToDefendRoll() {
        return toDefendRoll;
    }

    public void setToDefendRoll(Integer toDefendRoll) {
        this.toDefendRoll = toDefendRoll;
    }

    public Integer getForDamageRoll() {
        return forDamageRoll;
    }

    public void setForDamageRoll(Integer forDamageRoll) {
        this.forDamageRoll = forDamageRoll;
    }

    public Integer getForDeathCheckRoll() {
        return forDeathCheckRoll;
    }

    public void setForDeathCheckRoll(Integer forDeathCheckRoll) {
        this.forDeathCheckRoll = forDeathCheckRoll;
    }

    public Integer getForUnconsciousnessCheckRoll() {
        return forUnconsciousnessCheckRoll;
    }

    public void setForUnconsciousnessCheckRoll(Integer forUnconsciousnessCheckRoll) {
        this.forUnconsciousnessCheckRoll = forUnconsciousnessCheckRoll;
    }
}
