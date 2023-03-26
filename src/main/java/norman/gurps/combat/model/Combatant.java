package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Combatant {
    private String label;
    private GameChar gameChar;
    private Integer currentDamage;
    private Integer previousDamage;
    private Boolean unconsciousnessCheckFailed;
    private Integer nbrOfDeathChecksNeeded;
    private Boolean deathCheckFailed;
    private HealthStatus healthStatus;
    private Integer currentMove;
    private Integer defenseBonus;
    private Integer shockPenalty;
    private ActionType actionType;
    private String targetLabel;
    private String weaponName;
    private String weaponModeName;
    private Integer toHitEffectiveSkill;
    private Integer toHitRoll;
    private ResultType toHitResultType;
    private Integer damageDice;
    private Integer damageAdds;
    private Integer forDamageRoll;
    private List<CombatDefense> combatDefenses = new ArrayList<>();

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

    public Boolean getUnconsciousnessCheckFailed() {
        return unconsciousnessCheckFailed;
    }

    public void setUnconsciousnessCheckFailed(Boolean unconsciousnessCheckFailed) {
        this.unconsciousnessCheckFailed = unconsciousnessCheckFailed;
    }

    public Integer getNbrOfDeathChecksNeeded() {
        return nbrOfDeathChecksNeeded;
    }

    public void setNbrOfDeathChecksNeeded(Integer nbrOfDeathChecksNeeded) {
        this.nbrOfDeathChecksNeeded = nbrOfDeathChecksNeeded;
    }

    public Boolean getDeathCheckFailed() {
        return deathCheckFailed;
    }

    public void setDeathCheckFailed(Boolean deathCheckFailed) {
        this.deathCheckFailed = deathCheckFailed;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Integer getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Integer currentMove) {
        this.currentMove = currentMove;
    }

    public Integer getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(Integer defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public Integer getShockPenalty() {
        return shockPenalty;
    }

    public void setShockPenalty(Integer shockPenalty) {
        this.shockPenalty = shockPenalty;
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

    public Integer getToHitEffectiveSkill() {
        return toHitEffectiveSkill;
    }

    public void setToHitEffectiveSkill(Integer toHitEffectiveSkill) {
        this.toHitEffectiveSkill = toHitEffectiveSkill;
    }

    public Integer getToHitRoll() {
        return toHitRoll;
    }

    public void setToHitRoll(Integer toHitRoll) {
        this.toHitRoll = toHitRoll;
    }

    public ResultType getToHitResultType() {
        return toHitResultType;
    }

    public void setToHitResultType(ResultType toHitResultType) {
        this.toHitResultType = toHitResultType;
    }

    public Integer getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(Integer damageDice) {
        this.damageDice = damageDice;
    }

    public Integer getDamageAdds() {
        return damageAdds;
    }

    public void setDamageAdds(Integer damageAdds) {
        this.damageAdds = damageAdds;
    }

    public Integer getForDamageRoll() {
        return forDamageRoll;
    }

    public void setForDamageRoll(Integer forDamageRoll) {
        this.forDamageRoll = forDamageRoll;
    }

    public List<CombatDefense> getCombatDefenses() {
        return combatDefenses;
    }

    public void setCombatDefenses(List<CombatDefense> combatDefenses) {
        this.combatDefenses = combatDefenses;
    }
}
