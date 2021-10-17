package norman.gurps.combat;

import norman.gurps.character.GameCharacter;
import norman.gurps.strategy.StrategyHelper;

public class Combatant implements Comparable<Combatant> {
    private GameCharacter character;
    private String side;
    private StrategyHelper helper;
    private Action action;
    private Defense[] defenses;
    private int currentHitPoints;
    private int shockPenalty;
    private int damageTakenThisTurn;
    private CombatantStatus status = CombatantStatus.ALIVE;

    public GameCharacter getCharacter() {
        return character;
    }

    public void setCharacter(GameCharacter character) {
        this.character = character;
        // TODO Assumes battle begins with all characters at full hit points.
        currentHitPoints = character.getHitPoints();
    }

    @Override
    public int compareTo(Combatant other) {
        if (character.getBasicSpeed() != other.getCharacter().getBasicSpeed()) {
            return Double.compare(character.getBasicSpeed(), other.getCharacter().getBasicSpeed());
        } else {
            return Integer.compare(character.getDexterity(), other.character.getDexterity());
        }
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public StrategyHelper getHelper() {
        return helper;
    }

    public void setHelper(StrategyHelper helper) {
        this.helper = helper;
    }

    public void reset() {
        currentHitPoints -= damageTakenThisTurn;
        shockPenalty = Math.min(4, damageTakenThisTurn);
        damageTakenThisTurn = 0;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Defense[] getDefenses() {
        return defenses;
    }

    public void setDefenses(Defense[] defenses) {
        this.defenses = defenses;
    }

    public void applyDamage(int finalDamage) {
        damageTakenThisTurn += finalDamage;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints - damageTakenThisTurn;
    }

    public int getShockPenalty() {
        return Math.min(4, shockPenalty + damageTakenThisTurn);
    }

    public void setUnconscious() {
        status = CombatantStatus.UNCONSCIOUS;
    }

    public CombatantStatus getStatus() {
        if (status == CombatantStatus.UNCONSCIOUS) {
            return CombatantStatus.UNCONSCIOUS;
        } else if (getCurrentHitPoints() <= 0) {
            return CombatantStatus.COLLAPSING;
        } else if (getCurrentHitPoints() < character.getHitPoints() / 3.0) {
            return CombatantStatus.REELING;
        } else {
            return CombatantStatus.ALIVE;
        }
    }
}
