package tk.trentoleaf.cineweb.beans.rest.in;

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    @SerializedName("selected_seats")
    private List<SelectedSeat> selectedSeats;

    public CartItem() {
    }

    public Integer getFid() {
        return fid;
    }

    public Integer getPid() {
        return pid;
    }

    public Integer getRid() {
        return rid;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getTime() {
        return time;
    }

    public String getPlaybill() {
        return playbill;
    }

    public Integer getFree() {
        return free;
    }

    public List<TicketItem> getTickets() {
        return tickets;
    }

    public List<SelectedSeat> getSelectedSeats() {
        return selectedSeats;
    }

    public void setFree(Integer free) {
        this.free = free;
    }

    public void resetSeats() {
        this.selectedSeats = new ArrayList<>();
    }

    public int getTotalTickets() {
        int total = 0;
        for (TicketItem t : tickets) {
            total += t.getNumber();
        }
        return total;
    }

    public boolean isValidWithSeats() {
        return Utils.isValid(this) && selectedSeats != null && Utils.isValid(selectedSeats)
                && getTotalTickets() == selectedSeats.size();
    }

}
