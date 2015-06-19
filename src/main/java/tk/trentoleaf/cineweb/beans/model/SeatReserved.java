package tk.trentoleaf.cineweb.beans.model;


public class SeatReserved extends Seat
{
    private boolean reserved;

    public SeatReserved(int rid, int x, int y, boolean reserved) {
        super(rid, x, y);
        this.reserved=reserved;
    }

    public SeatReserved(int x, int y,boolean reserved) {
        super(x, y);
        this.reserved=reserved;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return "SeatReserved{" +
                "reserved=" + reserved +
                "} " + super.toString();
    }
}
