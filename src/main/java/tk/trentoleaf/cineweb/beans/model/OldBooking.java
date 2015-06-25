package tk.trentoleaf.cineweb.beans.model;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * OldBooking bean: represent a reservation for 1 seat for a given play.
 */
public class OldBooking implements Serializable {

    private int bid;
    private int rid;
    private int x;
    private int y;
    private int uid;
    private int pid;
    private DateTime timeBooking;
    private double price;

    public OldBooking() {
    }

    public OldBooking(int rid, int x, int y, int uid, int pid, DateTime timeBooking, double price) {
        this.rid = rid;
        this.x = x;
        this.y = y;
        this.uid = uid;
        this.pid = pid;
        this.timeBooking = timeBooking;
        this.price = price;
    }

    public OldBooking(int bid, int rid, int x, int y, int uid, int pid, DateTime timeBooking, double price) {
        this.bid = bid;
        this.rid = rid;
        this.x = x;
        this.y = y;
        this.uid = uid;
        this.pid = pid;
        this.timeBooking = timeBooking;
        this.price = price;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public DateTime getTimeBooking() {
        return timeBooking;
    }

    public void setTimeBooking(DateTime timeBooking) {
        this.timeBooking = timeBooking;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return bid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OldBooking oldBooking = (OldBooking) o;

        if (bid != oldBooking.bid) return false;
        if (rid != oldBooking.rid) return false;
        if (x != oldBooking.x) return false;
        if (y != oldBooking.y) return false;
        if (uid != oldBooking.uid) return false;
        if (pid != oldBooking.pid) return false;
        if (Double.compare(oldBooking.price, price) != 0) return false;
        return timeBooking.equals(oldBooking.timeBooking);
    }
}
