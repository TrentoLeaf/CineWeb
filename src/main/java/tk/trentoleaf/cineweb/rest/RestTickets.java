package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.annotations.rest.UserArea;
import tk.trentoleaf.cineweb.beans.model.Booking;
import tk.trentoleaf.cineweb.db.BookingsDB;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.rest.NotFoundException;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

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
