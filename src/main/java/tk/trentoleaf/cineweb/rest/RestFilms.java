package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.beans.model.Film;
import tk.trentoleaf.cineweb.beans.model.FilmGrossing;
import tk.trentoleaf.cineweb.db.FilmsDB;
import tk.trentoleaf.cineweb.db.StatisticsDB;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.ForeignKeyException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Films end point. Implements CRUD operations on the films.
 */
@Path("/films")
public class RestFilms {

    // DB singleton
    private FilmsDB filmsDB = FilmsDB.instance();
    private StatisticsDB statisticsDB = StatisticsDB.instance();

    @GET
    @Compress
    public List<Film> getFilms() {
        return filmsDB.getFilms();
    }

    @GET
    @Path("/future")
    @Compress
    public List<Film> getFutureFilms() {
        return filmsDB.getFutureFilms();
    }

    @GET
    @Path("/{id}")
    public Film getFilm(@PathParam("id") int fid) {
        try {
            return filmsDB.getFilm(fid);
        } catch (EntryNotFoundException e) {
            throw NotFoundException.FILM_NOT_FOUND;
        }
    }

    @GET
    @Path("/grossing")
    @Compress
    @AdminArea
    public List<FilmGrossing> getFilmsGrossing() {
        return statisticsDB.getFilmsGrossing();
    }

    @POST
    @AdminArea
    public Film addFilm(@NotNull(message = "Missing film object") @Valid Film film) {

        // add to filmsDB
        filmsDB.createFilm(film);

        return film;
    }

    @PUT
    @Path("/{id}")
    @AdminArea
    public Film editFilm(@PathParam("id") int id, @NotNull(message = "Missing film object") @Valid Film film) {

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
    public Response deleteFilm(@PathParam("id") int id) {

        // delete film
        try {
            filmsDB.deleteFilm(id);
            return Response.ok().build();
        } catch (EntryNotFoundException e) {
            throw NotFoundException.FILM_NOT_FOUND;
        } catch (ForeignKeyException e) {
            throw new ConflictException("Cannot delete this films because of existing plays");
        }
    }

}
