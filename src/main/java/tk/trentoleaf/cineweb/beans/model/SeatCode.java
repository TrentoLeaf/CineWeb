package tk.trentoleaf.cineweb.beans.model;

/**
 * Enumeration representing the status of a given Seat of a certain Room for a certain Play.
 *
 * @see Room
 * @see RoomStatus
 * @see Play
 */
public enum SeatCode {

    /**
     * The Seat is not present.
     */
    MISSING(0),

    /**
     * The Seat is present and free.
     */
    AVAILABLE(1),

    /**
     * The Seat is present but not free.
     */
    UNAVAILABLE(2),

    /**
     * Stats only: the seat is one of the top 10 seats in its Room.
     */
    BEST(3);

    private int value;

    /**
     * Construct a new SeatCode.
     *
     * @param value The value associated with this status.
     */
    SeatCode(int value) {
        this.value = value;
    }

    /**
     * Return the Integer value associated with this status.
     *
     * @return The Integer value associated with this status.
     */
    public int getValue() {
        return value;
    }
}
