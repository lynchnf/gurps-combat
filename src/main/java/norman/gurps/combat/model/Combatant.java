package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Combatant {
    private String label;
    private GameChar gameChar;
    private Integer currentDamage;
    private Integer previousDamage;
    private HealthStatus healthStatus;
    private Integer currentMove;
    private Action action;
    private String targetLabel;
    private String weaponName;
    private String modeName;
    private Integer effectiveSkillToHit;
    private Integer rollToHit;
    private SkillRollResult toHitResult;
    private Integer damageDice;
    private Integer damageAdds;
    private Integer rollForDamage;
    private List<ActiveDefense> activeDefenses = new ArrayList<>();

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

    public Integer getEffectiveSkillToHit() {
        return effectiveSkillToHit;
    }

    public void setEffectiveSkillToHit(Integer effectiveSkillToHit) {
        this.effectiveSkillToHit = effectiveSkillToHit;
    }

    public Integer getRollToHit() {
        return rollToHit;
    }

    public void setRollToHit(Integer rollToHit) {
        this.rollToHit = rollToHit;
    }

    public SkillRollResult getToHitResult() {
        return toHitResult;
    }

    public void setToHitResult(SkillRollResult toHitResult) {
        this.toHitResult = toHitResult;
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

    public Integer getRollForDamage() {
        return rollForDamage;
    }

    public void setRollForDamage(Integer rollForDamage) {
        this.rollForDamage = rollForDamage;
    }

    public List<ActiveDefense> getActiveDefenses() {
        return activeDefenses;
    }

    public void setActiveDefenses(List<ActiveDefense> activeDefenses) {
        this.activeDefenses = activeDefenses;
    }
}