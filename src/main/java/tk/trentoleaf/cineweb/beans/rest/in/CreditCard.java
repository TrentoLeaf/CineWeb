package tk.trentoleaf.cineweb.beans.rest.in;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;
import tk.trentoleaf.cineweb.annotations.hibernate.SafeString;

import javax.validation.constraints.*;

@SuppressWarnings("unused")
public class CreditCard {

    @SafeString(message = "Number cannot be null")
    @CreditCardNumber
    private String number;

    @SafeString(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Month cannot be null")
    @Min(value = 1, message = "Invalid month")
    @Max(value = 12, message = "Invalid month")
    private Integer month;

    @NotNull(message = "Year cannot be null")
    @Min(value = 2000, message = "Invalid month")
    @Max(value = 2100, message = "Invalid month")
    private Integer year;

    @SafeString(message = "Cvv cannot be null")
    @Digits(integer=4, fraction=0)
    @Length(min = 3, max = 4, message = "Invalid cvv")
    private String cvv;

    public CreditCard() {
    }

}
