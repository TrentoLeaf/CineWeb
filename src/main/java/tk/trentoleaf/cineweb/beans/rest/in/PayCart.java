package tk.trentoleaf.cineweb.beans.rest.in;

import tk.trentoleaf.cineweb.utils.Utils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuppressWarnings("unused")
public class PayCart {

    @NotNull(message = "Missing creditCard object")
    private CreditCard creditCard;

    @NotNull(message = "Missing cart object")
    @Valid
    private List<CartItem> cart;

    public PayCart() {
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public boolean isValidCart() {
        if (cart == null) {
            return false;
        }
        for (CartItem item : cart) {
            if (!item.isValidWithSeats()) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidCreditCard() {
        return creditCard != null && Utils.isValid(creditCard);
    }
}
