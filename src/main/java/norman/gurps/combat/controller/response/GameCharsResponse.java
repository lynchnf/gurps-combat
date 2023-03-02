package norman.gurps.combat.controller.response;

import norman.gurps.combat.model.GameChar;

import java.util.ArrayList;
import java.util.List;

public class GameCharsResponse {
    private Boolean successful;
    private List<String> messages = new ArrayList<>();
    private List<GameChar> gameChars = new ArrayList<>();

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<GameChar> getGameChars() {
        return gameChars;
    }

    public void setGameChars(List<GameChar> gameChars) {
        this.gameChars = gameChars;
    }
}
