package tk.trentoleaf.cineweb.beans.model;

import org.joda.time.DateTime;

/**
 * Bean that represent a bought ticket for a given seat. It belongs to a booking.
 */
public class Ticket {

    private int tid;
    private Integer bid;
    private Integer pid;
    private String title;
    private DateTime time;
    private int rid;
    private int x;
    private int y;
    private double price;
    private String type;
    private boolean deleted;

    public Ticket() {
    }

    public Ticket(int pid, int rid, int x, int y, double price, String type, boolean deleted) {
        this.pid = pid;
        this.rid = rid;
        this.x = x;
        this.y = y;
        this.price = price;
        this.type = type;
        this.deleted = deleted;
    }

    public Ticket(Play play, int x, int y, double price, String type) {
        this(play.getPid(), play.getRid(), x, y, price, type, false);
    }

    public Ticket(int tid, int bid, int pid, int rid, int x, int y, double price, String type, boolean deleted) {
        this.tid = tid;
        this.bid = bid;
        this.pid = pid;
        this.rid = rid;
        this.x = x;
        this.y = y;
        this.price = price;
        this.type = type;
        this.deleted = deleted;
    }

    public Ticket(int tid, int bid, Play play, int x, int y, double price, String type) {
        this(tid, bid, play.getPid(), play.getRid(), x, y, price, type, false);
    }

    public Ticket(int tid, int bid, Play play, int x, int y, double price, String type, boolean deleted) {
        this(tid, bid, play.getPid(), play.getRid(), x, y, price, type, deleted);
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        return tid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        if (tid != ticket.tid) return false;
        if (rid != ticket.rid) return false;
        if (x != ticket.x) return false;
        if (y != ticket.y) return false;
        if (Double.compare(ticket.price, price) != 0) return false;
        if (deleted != ticket.deleted) return false;
        if (bid != null ? !bid.equals(ticket.bid) : ticket.bid != null) return false;
        if (pid != null ? !pid.equals(ticket.pid) : ticket.pid != null) return false;
        if (title != null ? !title.equals(ticket.title) : ticket.title != null) return false;
        if (time != null ? !time.equals(ticket.time) : ticket.time != null) return false;
        return type.equals(ticket.type);
    }
}
