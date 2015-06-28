package tk.trentoleaf.cineweb.beans.model;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Booking bean: represent a reservation for 1 or more seats for some plays.
 */
public class Booking implements Serializable {

    private int bid;
    private int uid;
    private DateTime time;
    private double payedWithCredit;

    // tickets associated with this booking
    private List<Ticket> tickets;

    public Booking() {
    }

    public Booking(int bid, int uid, DateTime time, double payedWithCredit) {
        this.bid = bid;
        this.uid = uid;
        this.time = time;
        this.payedWithCredit = payedWithCredit;
    }

    public Booking(int bid, int uid, DateTime time, double payedWithCredit, List<Ticket> tickets) {
        this(bid, uid, time, payedWithCredit);
        this.tickets = tickets;
    }

    public Booking(int bid, User user, DateTime time, double payedWithCredit, List<Ticket> tickets) {
        this(bid, user.getUid(), time, payedWithCredit, tickets);
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public double getPayedWithCredit() {
        return payedWithCredit;
    }

    public void setPayedWithCredit(double payedWithCredit) {
        this.payedWithCredit = payedWithCredit;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public int hashCode() {
        return bid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        if (bid != booking.bid) return false;
        if (uid != booking.uid) return false;
        if (Double.compare(booking.payedWithCredit, payedWithCredit) != 0) return false;
        if (!time.equals(booking.time)) return false;
        return CollectionUtils.isEqualCollection(tickets, booking.tickets);
    }

}
