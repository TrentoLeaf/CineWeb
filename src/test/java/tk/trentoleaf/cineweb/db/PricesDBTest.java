package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.Price;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.NotNullViolationException;
import tk.trentoleaf.cineweb.exceptions.db.UniqueViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PricesDBTest extends DBTest {

    @Test
    public void getPriceOk() throws Exception {

        // create
        final Price p1 = new Price("A", 1);
        pricesDB.createPrice(p1);

        // retrieve
        final Price p2 = pricesDB.getPrice("a");

        // test
        assertEquals(p1, p2);
    }

    @Test(expected = EntryNotFoundException.class)
    public void getPriceFail() throws Exception {
        pricesDB.getPrice("aaaaaaa");
    }

    @Test
    public void loadDefaultPrices() throws Exception {

        // load default prices
        pricesDB.loadDefaultPrices();

        // current
        final List<Price> prices = pricesDB.getPrices();

        // test
        assertTrue(CollectionUtils.isEqualCollection(Price.DEFAULT_PRICES, prices));
    }

    @Test
    public void createPriceSuccess() throws Exception {

        // create prices
        final Price p1 = new Price("pippo", 3);
        final Price p2 = new Price("bbb", 45.34);
        pricesDB.createPrice(p1);
        pricesDB.createPrice(p2);

        // expected
        final List<Price> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);

        // current
        final List<Price> current = pricesDB.getPrices();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = UniqueViolationException.class)
    public void createPriceFail1() throws Exception {
        final Price p1 = new Price("pippo", 3);
        pricesDB.createPrice(p1);
        pricesDB.createPrice(p1);
    }

    @Test(expected = NotNullViolationException.class)
    public void createPriceFail2() throws Exception {
        final Price p1 = new Price(null, 3);
        pricesDB.createPrice(p1);
    }

    @Test
    public void updatePriceSuccess() throws Exception {

        // create price
        final Price price = new Price("price111", 1);
        pricesDB.createPrice(price);

        // edit price
        price.setPrice(34);
        pricesDB.updatePrice(price);

        // current
        final Price current = pricesDB.getPrice("price111");

        // test
        assertEquals(price, current);
    }

    @Test(expected = EntryNotFoundException.class)
    public void updatePriceFail() throws Exception {

        // edit price
        final Price price = new Price("price111", 1);
        pricesDB.updatePrice(price);
    }

    @Test
    public void deletePriceSuccess() throws Exception {

        // create prices
        final Price p1 = new Price("pippo", 3);
        final Price p2 = new Price("bbb", 45.34);
        pricesDB.createPrice(p1);
        pricesDB.createPrice(p2);

        // delete price
        pricesDB.deletePrice(p1);

        // expected
        final List<Price> expected = new ArrayList<>();
        expected.add(p2);

        // current
        final List<Price> current = pricesDB.getPrices();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = EntryNotFoundException.class)
    public void deletePriceFail() throws Exception {

        // delete price
        final Price p1 = new Price("pippo", 3);
        pricesDB.deletePrice(p1);
    }

}