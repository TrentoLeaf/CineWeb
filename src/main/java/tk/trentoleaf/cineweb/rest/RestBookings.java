package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.annotations.rest.Compress;
import tk.trentoleaf.cineweb.annotations.rest.UserArea;
import tk.trentoleaf.cineweb.beans.model.Booking;
import tk.trentoleaf.cineweb.db.BookingsDB;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.util.List;

/**
 * Bookings end point.
 */
@Path("/bookings")
public class RestBookings {

    // DB singleton
    private BookingsDB bookingsDB = BookingsDB.instance();

    @Context
    private HttpServletRequest request;

    @GET
    @AdminArea
    @Compress
    public List<Booking> getBookings() {
        return bookingsDB.getBookings();
    }

    @GET
    @Path("/{id}")
    @AdminArea
    @Compress
    public List<Booking> getUserBookings(@PathParam("id") int uid) {

        // return user bookings
        return bookingsDB.getBookingsByUser(uid);
    }

    @GET
    @Path("/my")
    @UserArea
    @Compress
    public List<Booking> getMyBookings() {
        final HttpSession session = request.getSession(false);
        assert session != null;

        final Integer uid = (Integer) session.getAttribute(Utils.UID);
        assert uid != null;

        // return my bookings
        return bookingsDB.getBookingsByUser(uid);
    }

}
