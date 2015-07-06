package tk.trentoleaf.cineweb.beans.rest.in;

import tk.trentoleaf.cineweb.annotations.hibernate.SafeString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class TicketItem {

    @SafeString(message = "Type must be a valid ticket type")
    private String type;

    private Double singleCost;

    @NotNull(message = "Message cannot be null")
    @Min(value = 1, message = "Cannot specify a quantity less than 1")
    private Integer number;

    public TicketItem() {
    }

    public String getType() {
        return type;
    }

    public Integer getNumber() {
        return number;
    }

    public void setSingleCost(Double singleCost) {
        this.singleCost = singleCost;
    }
}
