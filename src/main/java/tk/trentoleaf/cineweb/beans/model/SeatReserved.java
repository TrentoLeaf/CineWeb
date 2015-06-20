package tk.trentoleaf.cineweb.beans.model;

/**
 * This bean represent the status of a given seat. It can be reserved or not.
 */
public class SeatReserved extends Seat {

    private boolean reserved;

    public SeatReserved(int rid, int x, int y, boolean reserved) {
        super(rid, x, y);
        this.reserved = reserved;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
