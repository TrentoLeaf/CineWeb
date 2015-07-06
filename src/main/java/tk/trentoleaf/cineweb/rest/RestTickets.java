package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.db.BookingsDB;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Bookings end point.
 */
@Path("/tickets")
public class RestTickets {

    // DB singleton
    private BookingsDB bookingsDB = BookingsDB.instance();

    @DELETE
    @AdminArea
    @Path("/{tid}")
    public void deleteTicket(@PathParam("tid") int tid) {
        try {
            bookingsDB.deleteTicket(tid);
        } catch (EntryNotFoundException e) {
            throw NotFoundException.GENERIC;
        }
    }

}
