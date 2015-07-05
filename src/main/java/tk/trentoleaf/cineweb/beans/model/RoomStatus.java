package tk.trentoleaf.cineweb.beans.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @Min(value = 1, message = "A room must have at least 1 row")
    @Max(value = 50, message = "A room cat have at most 50 rows")
    private int rows;

    @Min(value = 1, message = "A room must have at least 1 column")
    @Max(value = 50, message = "A room cat have at most 50 columns")
    private int columns;

    @NotNull(message = "Seats cannot be null")
    private int[][] seats;

    /**
     * Construct a new empty RoomStatus.
     */
    public RoomStatus() {
    }

    /**
     * Constructor.
     *
     * @param rows    Number of rows in the Room.
     * @param columns Number of columns in the Room.
     * @param seats   Matrix of the seats (each number corresponds to a seat status).
     */
    public RoomStatus(int rows, int columns, int[][] seats) {
        this.rows = rows;
        this.columns = columns;
        this.seats = seats;
    }

    /**
     * Constructor.
     *
     * @param rid     Room ID.
     * @param rows    Number of rows in the Room.
     * @param columns Number of columns in the Room.
     * @param seats   Matrix of the seats (each number corresponds to a seat status).
     */
    public RoomStatus(int rid, int rows, int columns, int[][] seats) {
        this(rows, columns, seats);
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int[][] getSeats() {
        return seats;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setSeats(int[][] seats) {
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
