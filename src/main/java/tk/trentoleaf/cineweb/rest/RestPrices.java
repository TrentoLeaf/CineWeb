package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.beans.model.Price;
import tk.trentoleaf.cineweb.db.PricesDB;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.UniqueViolationException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 * Prices end point: implements CRUD operations on the Prices.
 */
@Path("/prices")
public class RestPrices {

    // rooms DB
    private final PricesDB pricesDB = PricesDB.instance();

    @GET
    public List<Price> getPrices() throws SQLException {
        return pricesDB.getPrices();
    }

    @POST
    @AdminArea
    public Price createPrice(@NotNull(message = "Missing price") @Valid Price price) {

        // make price case insensitive
        price.setType(price.getType().toLowerCase());

        // create price
        try {
            pricesDB.createPrice(price);
            return price;
        } catch (UniqueViolationException e) {
            throw new ConflictException("Price already exists");
        }
    }

    @PUT
    @Path("/{type}")
    @AdminArea
    public Price editPrice(@PathParam("type") String type, @NotNull(message = "Missing price") @Valid Price price) {

        // make price case insensitive
        price.setType(type.toLowerCase());

        // update price
        try {
            pricesDB.updatePrice(price);
            return price;
        } catch (EntryNotFoundException e) {
            throw new NotFoundException("Price not found");
        }
    }

    @DELETE
    @Path("/{type}")
    @AdminArea
    public Response deletePrice(@PathParam("type") String type) {

        // delete price
        try {
            pricesDB.deletePrice(type.toLowerCase());
            return Response.ok().build();
        } catch (EntryNotFoundException e) {
            throw new NotFoundException("Price not found");
        }
    }

}
