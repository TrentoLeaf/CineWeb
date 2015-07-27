package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.db.PricesDB;
import tk.trentoleaf.cineweb.db.UsersDB;
import tk.trentoleaf.cineweb.exceptions.rest.RestException;
import tk.trentoleaf.cineweb.utils.ExampleData;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Resources root.
 */
@Path("/")
public class RestRoot {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "Would you like a popcorn?";
    }

    @POST
    @Path("/load-example-data")
    @AdminArea
    public synchronized void loadExampleData(@NotNull(message = "No duration specified")
                                             @Min(value = 1, message = "Bad duration specified") Integer duration) {
        try {

            // re-init db
            DB.instance().reset();
            DB.instance().init();
            UsersDB.instance().createAdminUser();
            PricesDB.instance().loadDefaultPrices();

            // load example data
            ExampleData.loadExampleData(duration);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RestException(e.getMessage());
        }
    }
}
