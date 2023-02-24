package norman.gurps.combat.controller.response;

import norman.gurps.combat.model.GameChar;

import java.util.ArrayList;
import java.util.List;

public class ShowStoredCharsResponse {
    private Boolean successful;
    private String message;
    private List<GameChar> gameChars = new ArrayList<>();

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GameChar> getGameChars() {
        return gameChars;
    }

    public void setGameChars(List<GameChar> gameChars) {
        this.gameChars = gameChars;
    }
}
