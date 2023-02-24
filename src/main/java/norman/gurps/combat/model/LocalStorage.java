package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class LocalStorage {
    private List<GameChar> gameChars = new ArrayList<>();
    private Battle battle;

    public List<GameChar> getGameChars() {
        return gameChars;
    }

    public void setGameChars(List<GameChar> gameChars) {
        this.gameChars = gameChars;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }
}
