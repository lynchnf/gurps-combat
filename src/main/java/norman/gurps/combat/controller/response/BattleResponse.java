package norman.gurps.combat.controller.response;

import norman.gurps.combat.model.Battle;

import java.util.ArrayList;
import java.util.List;

public class BattleResponse {
    private Boolean successful;
    private List<String> messages = new ArrayList<>();
    private Battle battle;

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

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }
}
