package tk.trentoleaf.cineweb.beans.model;

import java.util.Arrays;

/**
 * This object represents the status of a certain room for a certain play. Each seat is represented with its
 * coordinates in the room. The status of a seat can be: MISSING (there is no seat in this position), AVAILABLE
 * (free), UNAVAILABLE (not free), BEST (used for statistics, one of the top 10 reserved seats for its room).
 *
 * @see SeatCode
 */
public class RoomStatus {

    private int rid;
    private int rows;
    private int columns;
    private int[][] seats;

    /**
     * Constructor.
     *
     * @param rid     Room ID.
     * @param rows    Number of rows in the Room.
     * @param columns Number of columns in the Room.
     * @param seats   Matrix of the seats (each number corresponds to a seat status).
     */
    public RoomStatus(int rid, int rows, int columns, int[][] seats) {
        this.rid = rid;
        this.rows = rows;
        this.columns = columns;
        this.seats = seats;
    }

    @Override
    public int hashCode() {
        return rid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomStatus that = (RoomStatus) o;

        if (rid != that.rid) return false;
        if (rows != that.rows) return false;
        if (columns != that.columns) return false;
        return Arrays.deepEquals(seats, that.seats);
    }

}
