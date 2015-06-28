package tk.trentoleaf.cineweb.beans.model;

import java.io.Serializable;

/**
 * Seat bean: represent a Seat in a cinema Room.
 */
public class Seat implements Serializable, Comparable {

    private int rid;
    private int x;
    private int y;

    /**
     * Construct a new Seat.
     *
     * @param x Number of row.
     * @param y Number of column.
     */
    public Seat(int x, int y) {
        this.rid = -1;
        this.x = x;
        this.y = y;
    }

    /**
     * Construct a new Seat.
     *
     * @param rid Room ID.
     * @param x   Number of row.
     * @param y   Number of column.
     */
    public Seat(int rid, int x, int y) {
        this.rid = rid;
        this.x = x;
        this.y = y;
    }

    public int getRid() {
        if (rid == -1) {
            throw new RuntimeException("Please use the constructor Seat(int rid, int x, int y) if you need a rid");
        }
        return rid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    @Override
    public int compareTo(Object other) {
        final Seat o = (Seat) other;

        // compare rid
        int ridDiff = this.rid - o.rid;
        if (ridDiff != 0) {
            return ridDiff;
        }

        // compare row (x)
        int xDiff = this.x - o.x;
        if (xDiff != 0) {
            return xDiff;
        }

        // compare col (y)
        return this.y - o.y;
    }

    @Override
    public int hashCode() {
        int result = rid;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        if (rid != seat.rid) return false;
        if (x != seat.x) return false;
        return y == seat.y;
    }
}
