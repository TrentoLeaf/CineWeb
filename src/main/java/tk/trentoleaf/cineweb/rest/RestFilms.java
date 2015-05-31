package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.sql.SQLException;
import java.util.List;

@Path("/films")
public class RestFilms {

    // db singleton
    private DB db = DB.instance();

    // TODO: add filter
    @GET
    public List<Film> getFilms() throws SQLException {
        return db.getFilms();
    }


}
