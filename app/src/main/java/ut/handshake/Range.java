package ut.handshake;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Aurelius on 12/14/14.
 */
public class Range implements Serializable {

    GregorianCalendar start;
    GregorianCalendar end;
    GregorianCalendar repeatInterval;
    GregorianCalendar cutoff;

    public Range(GregorianCalendar start, GregorianCalendar end, GregorianCalendar repeatInterval, GregorianCalendar cutoff) {
        this.start = start;
        this.end = end;
        this.repeatInterval = repeatInterval;
        this.cutoff = cutoff;
    }

    public GregorianCalendar getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(GregorianCalendar repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public GregorianCalendar getCutoff() {
        return cutoff;
    }

    public void setCutoff(GregorianCalendar cutoff) {
        this.cutoff = cutoff;
    }

    public GregorianCalendar getStart() {
        return start;
    }

    public void setStart(GregorianCalendar start) {
        this.start = start;
    }

    public void setUtcStart(Date start) {
        this.start.setTime(start);
    }

    public GregorianCalendar getEnd() {
        return end;
    }

    public void setEnd(GregorianCalendar end) {
        this.end = end;
    }

    public void setUtcEnd(Date end) {
        this.end.setTime(end);
    }

    public Date getUtcStart() { return start.getTime(); }

    public Date getUtcEnd() { return end.getTime(); }
}
