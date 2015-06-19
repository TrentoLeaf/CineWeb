package tk.trentoleaf.cineweb.beans.model;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {

    private int rid;
    private int rows;
    private int columns;

    private transient List<Seat> seats;

    public Room() {
    }

    public Room(int rid, int rows, int columns) {
        this.rid = rid;
        this.rows = rows;
        this.columns = columns;
        this.seats = new ArrayList<>();
    }

    public Room(int rid, int rows, int columns, List<Seat> seats) {
        this.rid = rid;
        this.rows = rows;
        this.columns = columns;
        this.seats = seats;
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

    public List<Seat> getSeats() {
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

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void addSeats(Seat seat) {
        seats.add(seat);
    }

    @Override
    public int hashCode() {
        return rid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (rid != room.rid) return false;
        if (rows != room.rows) return false;
        if (columns != room.columns) return false;
        return CollectionUtils.isEqualCollection(this.seats, room.seats);
    }

    @Override
    public String toString() {
        return "Room{" +
                "rid=" + rid +
                ", rows=" + rows +
                ", columns=" + columns +
                ", seats=" + seats +
                '}';
    }
}
