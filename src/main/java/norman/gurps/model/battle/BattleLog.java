package norman.gurps.model.battle;

import java.text.DateFormat;
import java.util.Date;

public class BattleLog {
    public static DateFormat FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    private Date timestamp = new Date();
    private String message;
    private String battleJson;

    public BattleLog(String message, String battleJson) {
        this.message = message;
        this.battleJson = battleJson;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBattleJson() {
        return battleJson;
    }

    public void setBattleJson(String battleJson) {
        this.battleJson = battleJson;
    }

    @Override
    public String toString() {
        return FORMAT.format(timestamp) + " - " + message;
    }
}
