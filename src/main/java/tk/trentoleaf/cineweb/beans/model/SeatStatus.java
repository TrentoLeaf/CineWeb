package tk.trentoleaf.cineweb.beans.model;

/**
 * This bean represent the status of a given seat of a certain room. It can be reserved or not.
 */
public class SeatStatus extends Seat {

    private boolean reserved;

    public SeatStatus(int rid, int x, int y, boolean reserved) {
        super(rid, x, y);
        this.reserved = reserved;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (reserved ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SeatStatus that = (SeatStatus) o;

        return reserved == that.reserved;
    }
}
