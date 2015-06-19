package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.db.PlaysDB;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.exceptions.db.ConstrainException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;
import tk.trentoleaf.cineweb.beans.model.Play;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 * Plays end point. Implements CRUD operations on the plays.
 */
@Path("/plays")
public class RestPlays {

    // playsDB singleton
    private PlaysDB playsDB = PlaysDB.instance();

    @GET
    @Compress
    public List<Play> getPlays() throws SQLException {
        return playsDB.getPlays();
    }

    @GET
    @Path("/{id}")
    public Play getPlay(@PathParam("id") int id) throws SQLException {
        try {
            return playsDB.getPlay(id);
        } catch (EntryNotFoundException e) {
            throw NotFoundException.PLAY_NOT_FOUND;
        }
    }

    @POST
    @AdminArea
    public Play createPlay(@NotNull(message = "Missing play object") @Valid Play play) throws SQLException {

        // add play to playsDB
        try {
            playsDB.createPlay(play);
            return play;
        } catch (ConstrainException e) {
            throw new BadRequestException("Fid or rid not found");
        } catch (AnotherFilmScheduledException e1) {
            throw ConflictException.ANOTHER_FILM;
        }
    }

    // TODO: cannot edit play --> OK?

    @DELETE
    @Path("/{id}")
    @AdminArea
    public Response deletePlay(@PathParam("id") int id) throws SQLException {

        // try to delete the play
        try {
            playsDB.deletePlay(id);
            return Response.ok().build();
        } catch (EntryNotFoundException e) {
            throw NotFoundException.PLAY_NOT_FOUND;
        }
    }

}
