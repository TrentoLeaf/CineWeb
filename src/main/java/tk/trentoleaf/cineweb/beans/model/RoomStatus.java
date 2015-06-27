package tk.trentoleaf.cineweb.beans.model;

import java.util.Arrays;

public class RoomStatus {

    private int rid;
    private int rows;
    private int columns;
    private int[][] seats;

    public RoomStatus() {
    }

    public RoomStatus(int rid, int rows, int columns, int[][] seats) {
        this.rid = rid;
        this.rows = rows;
        this.columns = columns;
        this.seats = seats;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int[][] getSeats() {
        return seats;
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
