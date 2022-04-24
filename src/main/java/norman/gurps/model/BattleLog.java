package norman.gurps.model;

import java.text.DateFormat;
import java.util.Date;

public class BattleLog {
    public static final DateFormat FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    private Date timestamp = new Date();
    private String message;

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return FORMAT.format(timestamp) + " - " + message;
    }
}
