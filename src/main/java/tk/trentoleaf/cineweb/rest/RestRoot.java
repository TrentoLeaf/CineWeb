package tk.trentoleaf.cineweb.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestRoot {
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "Jersey Working!";
    }
}
