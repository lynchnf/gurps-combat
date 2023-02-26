package norman.gurps.combat.controller.response;

import norman.gurps.combat.model.Battle;

public class ShowBattleResponse {
    private Boolean successful;
    private String message;
    private Battle battle;

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

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }
}
