package tk.trentoleaf.cineweb.beans.rest.in;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuppressWarnings("unused")
public class Cart {

    @NotNull(message = "Missing cart object")
    @Valid
    private List<CartItem> cart;

    public Cart() {
    }

    public List<CartItem> getCart() {
        return cart;
    }
}
