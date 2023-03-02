package norman.gurps.combat.controller.response;

import java.util.ArrayList;
import java.util.List;

public class BasicResponse {
    private Boolean successful;
    private List<String> messages = new ArrayList<>();

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
}
