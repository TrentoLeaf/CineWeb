package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.db.PricesDB;
import tk.trentoleaf.cineweb.db.UsersDB;
import tk.trentoleaf.cineweb.exceptions.rest.RestException;
import tk.trentoleaf.cineweb.utils.ExampleData;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public synchronized void loadExampleData() {
        try {

            // re-init db
            DB.instance().reset();
            DB.instance().init();
            UsersDB.instance().createAdminUser();
            PricesDB.instance().loadDefaultPrices();

            // load example data
            ExampleData.loadExampleData(100);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RestException(e.getMessage());
        }
    }
}
