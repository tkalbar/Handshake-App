package ut.handshake;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aurelius on 12/13/14.
 */
public class Conversation implements Serializable {

    private String otherActualDisplayName;
    private String otherDisplayName;
    private String otherId;
    private ArrayList<Message> messages;

    public Conversation (String otherActualDisplayName, String otherDisplayName, String otherId, ArrayList<Message> messages) {
        this.otherActualDisplayName = otherActualDisplayName;
        this.otherDisplayName = otherDisplayName;
        this.otherId = otherId;
        this.messages = messages;
    }


    public String getOtherActualDisplayName() {
        return otherActualDisplayName;
    }

    public void setOtherActualDisplayName(String otherActualDisplayName) {
        this.otherActualDisplayName = otherActualDisplayName;
    }

    public String getOtherDisplayName() {
        return otherDisplayName;
    }

    public void setOtherDisplayName(String otherDisplayName) {
        this.otherDisplayName = otherDisplayName;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
