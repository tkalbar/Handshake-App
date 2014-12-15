package ut.handshake;

import android.util.Log;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Aurelius on 12/13/14.
 */
public class Route implements Serializable {

    public static final String OWN_ROUTE_TYPE = "own_route";
    public static final String JOIN_ROUTE_TYPE = "join_route";

    private ArrayList<Range> validRanges;
    private String owner;
    //private ArrayList<String> phonesNumbers;
    //private ArrayList<String> emails;
    private JSONArray emails;
    private JSONArray phoneNumbers;
    private String routeId;
    private String displayName;
    private boolean isOnline;
    private String type;
    private ArrayList<Conversation> conversations;

    private static final String TAG = Route.class.getSimpleName();

    public Route(ArrayList<Range> validRanges, String owner, JSONArray phoneNumbers,
                 JSONArray emails, String routeId, String displayName, boolean isOnline, String type, ArrayList<Conversation> conversations) {
        this.validRanges = validRanges;
        this.owner = owner;
        this.phoneNumbers = phoneNumbers;
        this.emails = emails;
        this.routeId = routeId;
        this.displayName = displayName;
        this.isOnline = isOnline;
        this.type = type;
        this.conversations = conversations;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /*public boolean isOnline() {
        isOnline = false;
        Log.d(TAG, "Checking online");
        int count = 0;
        for (Range range : validRanges) {
            Log.d(TAG, "Found range "+count);
            Log.d(TAG, "Start: "+range.getUtcStart().getTime());
            Log.d(TAG, "End: " + range.getUtcEnd().getTime());
            Date cur = new Date();
            Log.d(TAG, "Current: "+cur.getTime());
            if (range.getUtcStart().getTime() <= cur.getTime() && cur.getTime() <= range.getUtcEnd().getTime()) {
                Log.d(TAG, "IsOnline");
                isOnline = true;
                return isOnline;
            }
            count++;
        }
        return isOnline;
    }*/

    public ArrayList<Range> getValidRanges() {
        return validRanges;
    }

    public void setValidRanges(ArrayList<Range> validRanges) {
        this.validRanges = validRanges;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public JSONArray getEmails() {
        return emails;
    }

    public void setEmails(JSONArray emails) {
        this.emails = emails;
    }

    public JSONArray getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(JSONArray phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

}
