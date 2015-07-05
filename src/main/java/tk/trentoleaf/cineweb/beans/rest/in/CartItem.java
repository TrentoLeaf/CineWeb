package tk.trentoleaf.cineweb.beans.rest.in;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CartItem {

    private Integer fid;

    @NotNull(message = "Pid cannot be null")
    private Integer pid;

    private Integer rid;
    private String title;
    private DateTime time;
    private String playbill;
    private Integer free;

    @NotNull(message = "Tickets cannot be null")
    private List<TicketItem> tickets;

    public CartItem() {
    }

    public Integer getPid() {
        return pid;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public int getTotalTickets() {
        int total = 0;
        for (TicketItem t : tickets) {
            total += t.getNumber();
        }
        return total;
    }

}
