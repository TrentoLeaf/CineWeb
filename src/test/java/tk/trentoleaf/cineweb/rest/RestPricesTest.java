package tk.trentoleaf.cineweb.rest;

import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.Price;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestPricesTest extends MyJerseyTest {

    @Test
    public void getPrices() throws Exception {

        // create prices
        final Price price = new Price("aa", 1);
        final Price price2 = new Price("BasB", 1);
        pricesDB.createPrice(price);
        pricesDB.createPrice(price2);

        // list of prices
        final List<Price> prices = new ArrayList<>();
        prices.add(price);
        prices.add(price2);

        // get prices
        final Response r1 = getTarget().path("/prices").request(JSON).get();
        assertEquals(200, r1.getStatus());
        assertEquals(prices, r1.readEntity(new GenericType<List<Price>>() {
        }));
    }

    @Test
    public void addPriceSuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // price to create
        final Price price = new Price("aa", 1);

        // create price
        final Response r1 = getTarget().path("/prices").request(JSON).cookie(c).post(Entity.json(price));
        assertEquals(200, r1.getStatus());

        // get created film
        final Price created = r1.readEntity(Price.class);

        // compare
        assertEquals(price, created);
    }

    @Test
    public void addPriceFail1() throws Exception {

        // login as admin
        final Cookie c = loginClient();

        // price to create
        final Price price = new Price("aa", 1);

        // create price
        final Response r1 = getTarget().path("/prices").request(JSON).cookie(c).post(Entity.json(price));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void addPriceFail2() throws Exception {

        // no login

        // price to create
        final Price price = new Price("aa", 1);

        // create price
        final Response r1 = getTarget().path("/prices").request(JSON).post(Entity.json(price));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void addPriceFail3() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // film to create
        final Price price = new Price("", 1);

        // create price
        final Response r1 = getTarget().path("/prices").request(JSON).cookie(c).post(Entity.json(price));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void addPriceFail4() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // no film provided
        // create film
        final Response r1 = getTarget().path("/prices").request(JSON).cookie(c).post(null);
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void editPriceSuccess() throws Exception {

        // create prices
        final Price p1 = new Price("aa", 45);
        pricesDB.createPrice(p1);
        final Price p2 = new Price("aa", 2);

        // login as admin
        final Cookie c = loginAdmin();

        // update price
        final Response r1 = getTarget().path("/prices/" + p1.getType()).request(JSON).cookie(c).put(Entity.json(p2));
        assertEquals(200, r1.getStatus());

        // test
        assertEquals(pricesDB.getPrice("aa"), p2);
    }

    @Test
    public void editPriceFail1() throws Exception {

        // create prices
        final Price p1 = new Price("aa", 45);

        // login as client
        final Cookie c = loginClient();

        // update price
        final Response r1 = getTarget().path("/prices/" + "aa").request(JSON).cookie(c).put(Entity.json(p1));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void editPriceFail2() throws Exception {

        // create prices
        final Price p1 = new Price("aa", 45);

        // no login

        // update film
        final Response r1 = getTarget().path("/prices/" + 343).request(JSON).put(Entity.json(p1));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void editPriceFail3() throws Exception {

        // price to edit
        final Price p1 = new Price("aa", 45);

        // login as admin
        final Cookie c = loginAdmin();

        // update price -> should fail (not found)
        final Response r1 = getTarget().path("/prices/" + "aaa").request(JSON).cookie(c).put(Entity.json(p1));
        assertEquals(404, r1.getStatus());
    }

    @Test(expected = EntryNotFoundException.class)
    public void deletePriceSuccess() throws Exception {

        // create price to delete
        final Price p1 = new Price("AA", 45);
        pricesDB.createPrice(p1);

        // login as admin
        final Cookie c = loginAdmin();

        // delete price
        final Response r1 = getTarget().path("/prices/" + p1.getType()).request(JSON).cookie(c).delete();
        assertEquals(200, r1.getStatus());

        // should throw an exception
        pricesDB.getPrice(p1.getType());
    }

    @Test
    public void deletePriceFail1() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // delete user
        final Response r1 = getTarget().path("/prices/" + 234).request(JSON).cookie(c).delete();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void deletePriceFail2() throws Exception {

        // create price to delete
        final Price p1 = new Price("AA", 45);
        pricesDB.createPrice(p1);

        // login as client
        final Cookie c = loginClient();

        // delete price
        final Response r1 = getTarget().path("/prices/" + p1.getType()).request(JSON).cookie(c).delete();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void deletePriceFail3() throws Exception {

        // create price to delete
        final Price p1 = new Price("AA", 45);
        pricesDB.createPrice(p1);

        // no login

        // delete price
        final Response r1 = getTarget().path("/prices/" + p1.getType()).request(JSON).delete();
        assertEquals(401, r1.getStatus());
    }

}

