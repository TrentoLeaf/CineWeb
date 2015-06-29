package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.beans.model.Room;
import tk.trentoleaf.cineweb.beans.model.RoomStatus;
import tk.trentoleaf.cineweb.db.RoomsDB;
import tk.trentoleaf.cineweb.db.StatisticsDB;
import tk.trentoleaf.cineweb.exceptions.db.BadRoomException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

/**
 * Rooms end point. Implements CRUD operations on the rooms.
 */
@Path("/rooms")
public class RestRooms {

    // DB singleton
    private RoomsDB roomsDB = RoomsDB.instance();
    private StatisticsDB statisticsDB = StatisticsDB.instance();

    @GET
    @AdminArea
    @Compress
    public List<Room> getRooms() {
        return roomsDB.getRooms(false);
    }

    // TODO -> test
    @GET
    @Path("/{id}")
    @AdminArea
    @Compress
    public RoomStatus getTopSeats(@PathParam("id") int id) {
        try {
            return statisticsDB.getTopSeatsByRoom(id);
        } catch (EntryNotFoundException e) {
            throw NotFoundException.GENERIC;
        }
    }

    @POST
    @AdminArea
    public RoomStatus createRoom(@NotNull(message = "Bad room object") @Valid RoomStatus room) {
        try {
            roomsDB.createRoom(room);
            return room;
        } catch (BadRoomException e) {
            throw new BadRequestException("Bad room");
        }
    }

}
