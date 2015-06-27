package tk.trentoleaf.cineweb.beans.model;

public enum SeatCode {

    MISSING(0),
    AVAILABLE(1),
    UNAVAILABLE(2),
    BEST(3);

    private int value;

    SeatCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
