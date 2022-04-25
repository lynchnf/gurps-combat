package norman.gurps.model;

public class Combatant {
    private GameChar gameChar;

    public Combatant(GameChar gameChar) {
        this.gameChar = gameChar;
    }

    public GameChar getGameChar() {
        return gameChar;
    }

    public void setGameChar(GameChar gameChar) {
        this.gameChar = gameChar;
    }

    @Override
    public String toString() {
        return "Combatant{" + "gameChar=" + gameChar + '}';
    }
}
