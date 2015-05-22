package tk.trentoleaf.cineweb.model;

import java.io.Serializable;

public class Place implements Serializable {

    private int row;
    private int column;

    public Place() {
    }

    public Place(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (row != place.row) return false;
        return column == place.column;
    }

    @Override
    public String toString() {
        return "Place{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
