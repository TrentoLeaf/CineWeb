package tk.trentoleaf.cineweb.beans.rest.in;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuppressWarnings("unused")
public class CheckCart {

    @NotNull(message = "Missing cart object")
    @Valid
    private List<CartItem> cart;

    public CheckCart() {
    }

    public List<CartItem> getCart() {
        return cart;
    }
}
