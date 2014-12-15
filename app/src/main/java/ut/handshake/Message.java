package ut.handshake;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Message implements Serializable {

    String messageBody;
    String from;
    String routeId;
	boolean isMine;
    String messageId;
    GregorianCalendar messageTime;

	public Message(String messageBody, String from, String routeId,
                   boolean isMine, String messageId, Date timestamp) {
		this.messageBody = messageBody;
        this.from = from;
        this.routeId = routeId;
		this.isMine = isMine;
        this.messageId = messageId;
        messageTime = new GregorianCalendar();
        messageTime.setTime(timestamp);
	}

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public GregorianCalendar getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(GregorianCalendar messageTime) {
        this.messageTime = messageTime;
    }
}
