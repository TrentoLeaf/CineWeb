package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/plays")
public class RestPlays {

    // db singleton
    private DB db = DB.instance();

    // TODO: add filter
    @GET
    public List<Play> getPlays() throws SQLException {
        return db.getPlays();
    }


}
