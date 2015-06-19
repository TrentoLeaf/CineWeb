package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.db.FilmsDB;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;
import tk.trentoleaf.cineweb.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/films")
public class RestFilms {

    // filmsDB singleton
    private FilmsDB filmsDB = FilmsDB.instance();

    @GET
    public List<Film> getFilms() throws SQLException {
        return filmsDB.getFilms();
    }

    @GET
    @Path("/{id}")
    public Film getFilm(@PathParam("id") int fid) throws SQLException {
        try {
            return filmsDB.getFilm(fid);
        } catch (EntryNotFoundException e) {
            throw NotFoundException.FILM_NOT_FOUND;
        }
    }

    @POST
    @AdminArea
    public Film addFilm(@NotNull(message = "Missing film object") @Valid Film film) throws SQLException {

        // add to filmsDB
        filmsDB.createFilm(film);

        return film;
    }

    @PUT
    @Path("/{id}")
    @AdminArea
    public Film editFilm(@PathParam("id") int id, @NotNull(message = "Missing film object") @Valid Film film) throws SQLException {

        // update film
        try {
            film.setFid(id);
            filmsDB.updateFilm(film);
            return film;
        } catch (EntryNotFoundException e) {
            throw NotFoundException.FILM_NOT_FOUND;
        }

    }

    @DELETE
    @Path("/{id}")
    @AdminArea
    public Response deleteUser(@PathParam("id") int id) throws SQLException {

        // delete film
        try {
            filmsDB.deleteFilm(id);
            return Response.ok().build();
        } catch (EntryNotFoundException e) {
            throw NotFoundException.FILM_NOT_FOUND;
        }

    }

}
