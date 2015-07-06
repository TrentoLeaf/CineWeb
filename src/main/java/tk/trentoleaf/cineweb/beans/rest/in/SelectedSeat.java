package tk.trentoleaf.cineweb.beans.rest.in;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class SelectedSeat {

    @NotNull(message = "Missing row")
    @Min(value = 0, message = "Row cannot be negative")
    private Integer row;

    @NotNull(message = "Missing col")
    @Min(value = 0, message = "Col cannot be negative")
    private Integer col;

    public SelectedSeat() {
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }
}
