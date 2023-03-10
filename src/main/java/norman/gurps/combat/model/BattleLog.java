package norman.gurps.combat.model;

import java.util.Date;

public class BattleLog {
    private Long timeMillis;
    private String message;

    public BattleLog() {
    }

    public BattleLog(String message) {
        timeMillis = System.currentTimeMillis();
        this.message = message;
    }

    public Long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(Long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%tr: %s", new Date(timeMillis), message);
    }
}