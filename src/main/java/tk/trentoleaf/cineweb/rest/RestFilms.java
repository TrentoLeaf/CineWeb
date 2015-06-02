package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;
import tk.trentoleaf.cineweb.rest.exceptions.BadRequestException;
import tk.trentoleaf.cineweb.rest.exceptions.NotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/films")
public class RestFilms {

    // db singleton
    private DB db = DB.instance();

    @GET
    public List<Film> getFilms() throws SQLException {
        return db.getFilms();
    }

    @GET
    @Path("/{id}")
    public Film getFilm(@PathParam("id") int fid) throws SQLException {
        try {
            return db.getFilm(fid);
        } catch (EntryNotFoundException e) {
            throw NotFoundException.FILM_NOT_FOUND;
        }
    }

    @POST
    public Film addFilm(Film film) throws SQLException {

        // validate
        if (film == null || !film.isValid()) {
            throw BadRequestException.BAD_FILM;
        }

        // add to db
        db.createFilm(film);

        return film;
    }

}
