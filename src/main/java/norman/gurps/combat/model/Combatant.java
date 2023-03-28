package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Combatant {
    private String label;
    private GameChar gameChar;
    private List<String> readyItems = new ArrayList<>();
    private Integer currentDamage;
    private Integer previousDamage;
    private Boolean unconsciousnessCheckFailed;
    private Integer nbrOfDeathChecksNeeded;
    private Boolean deathCheckFailed;
    private HealthStatus healthStatus;
    private Integer currentMove;
    private Integer shockPenalty;
    private ActionType actionType;
    private List<CombatMelee> combatMelees = new ArrayList<>();
    private CombatRanged combatRanged;
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

    public List<String> getReadyItems() {
        return readyItems;
    }

    public void setReadyItems(List<String> readyItems) {
        this.readyItems = readyItems;
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

    public List<CombatMelee> getCombatMelees() {
        return combatMelees;
    }

    public void setCombatMelees(List<CombatMelee> combatMelees) {
        this.combatMelees = combatMelees;
    }

    public CombatRanged getCombatRanged() {
        return combatRanged;
    }

    public void setCombatRanged(CombatRanged combatRanged) {
        this.combatRanged = combatRanged;
    }

    public List<CombatDefense> getCombatDefenses() {
        return combatDefenses;
    }

    public void setCombatDefenses(List<CombatDefense> combatDefenses) {
        this.combatDefenses = combatDefenses;
    }
}
