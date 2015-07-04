package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.beans.model.Room;
import tk.trentoleaf.cineweb.beans.model.RoomStatus;
import tk.trentoleaf.cineweb.db.RoomsDB;
import tk.trentoleaf.cineweb.db.StatisticsDB;
import tk.trentoleaf.cineweb.exceptions.db.BadRoomException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.ForeignKeyException;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.exceptions.rest.ConflictException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
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

    @PUT
    @Path("/{id}")
    @AdminArea
    public RoomStatus editRoom(@PathParam("id") int id, @NotNull(message = "Bad room object") @Valid RoomStatus room) {
        try {
            room.setRid(id);
            roomsDB.editRoom(room);
            return room;
        } catch (BadRoomException e) {
            throw new BadRequestException("Bad room");
        } catch (ForeignKeyException e) {
            throw new ConflictException("Cannot edit this room: there are reserved seats.");
        }
    }

    @DELETE
    @Path("/{id}")
    @AdminArea
    public void deleteRoom(@PathParam("id") int id) {
        try {
            roomsDB.deleteRoom(id);
        } catch (EntryNotFoundException e) {
            throw new NotFoundException("Room not found");
        } catch (ForeignKeyException e) {
            throw new ConflictException("Cannot delete this room: there are reserved seats.");
        }
    }

}
