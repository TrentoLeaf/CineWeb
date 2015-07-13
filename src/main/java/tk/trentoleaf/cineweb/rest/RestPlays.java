package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.beans.model.Play;
import tk.trentoleaf.cineweb.beans.model.RoomStatus;
import tk.trentoleaf.cineweb.db.PlaysDB;
import tk.trentoleaf.cineweb.db.RoomsDB;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.ForeignKeyException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Plays end point. Implements CRUD operations on the plays.
 */
@Path("/plays")
public class RestPlays {

    // DB singleton
    private PlaysDB playsDB = PlaysDB.instance();
    private RoomsDB roomsDB = RoomsDB.instance();

    @GET
    @Compress
    public List<Play> getPlays() {
        return playsDB.getPlays();
    }

    @GET
    @Path("/future")
    @Compress
    public List<Play> getNotGonePlays() {
        return playsDB.getFuturePlays();
    }

    @GET
    @Path("/{id}")
    public Play getPlay(@PathParam("id") int id) {
        try {
            return playsDB.getPlay(id);
        } catch (EntryNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @POST
    @AdminArea
    public Play createPlay(@NotNull(message = "Missing play object") @Valid Play play) {

        // add play to playsDB
        try {
            playsDB.createPlay(play);
            return play;
        } catch (ForeignKeyException e) {
            throw new BadRequestException("Fid or rid not found");
        } catch (AnotherFilmScheduledException e) {
            throw new ConflictException("Another film already scheduled");
        }
    }

    @DELETE
    @Path("/{id}")
    @AdminArea
    public Response deletePlay(@PathParam("id") int id) {

        // try to delete the play
        try {
            playsDB.deletePlay(id);
            return Response.ok().build();
        } catch (EntryNotFoundException e) {
            throw new NotFoundException();
        } catch (ForeignKeyException e) {
            throw new ConflictException("Cannot delete this play: there are booked seats");
        }
    }

    @GET
    @Path("/{id}/room")
    public RoomStatus getRoomStatusByPlay(@PathParam("id") int id) {
        try {
            return roomsDB.getRoomStatusByPlay(id);
        } catch (EntryNotFoundException e) {
            throw new NotFoundException();
        }
    }

}
