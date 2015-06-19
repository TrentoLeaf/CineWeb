package tk.trentoleaf.cineweb.beans.model;

import tk.trentoleaf.cineweb.annotations.hibernate.SafeString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Price bean: it represent the price for a given category.
 */
public class Price implements Serializable {

    // default prices
    public static final List<Price> DEFAULT_PRICES;

    static {
        DEFAULT_PRICES = new ArrayList<>();
        DEFAULT_PRICES.add(new Price("normal", 8.5));
        DEFAULT_PRICES.add(new Price("reduced", 5.5));
        DEFAULT_PRICES.add(new Price("military", 6));
        DEFAULT_PRICES.add(new Price("disabled", 7));
    }

    @SafeString(message = "Type must be a valid string")
    private String type;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    /**
     * Construct a new Price.
     */
    public Price() {
    }

    /**
     * Construct a new Price.
     *
     * @param type  Price type.
     * @param price Price.
     */
    public Price(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price1 = (Price) o;

        if (Double.compare(price1.price, price) != 0) return false;
        return type.equals(price1.type);
    }
}
