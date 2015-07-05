package tk.trentoleaf.cineweb.rest;

import tk.trentoleaf.cineweb.beans.model.Play;
import tk.trentoleaf.cineweb.beans.rest.in.Cart;
import tk.trentoleaf.cineweb.beans.rest.in.CartItem;
import tk.trentoleaf.cineweb.db.PlaysDB;
import tk.trentoleaf.cineweb.db.RoomsDB;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Iterator;

/**
 * Buy procedure end point.
 */
@Path("/buy")
public class RestBuy {

    // DB singleton
    private PlaysDB playsDB = PlaysDB.instance();
    private RoomsDB roomsDB = RoomsDB.instance();

    @POST
    @Path("/proceed")
    public Response checkCart(@NotNull(message = "Missing cart") @Valid Cart cart) {

        // there was something wrong in the cart (eg. old plays etc)
        boolean error = false;

        Iterator<CartItem> iterator = cart.getCart().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();

            // check current play
            try {

                // get play
                int pid = item.getPid();
                Play current = playsDB.getPlay(pid);

                // check if play already started
                if (current.getTime().isBeforeNow()) {

                    // play gone
                    iterator.remove();
                    error = true;
                }

                // check if enough seats
                int availableSeats = roomsDB.freePlacesByPlay(pid);
                int requestedSeats = item.getTotalTickets();

                // if not enough seats -> update free field
                if (availableSeats < requestedSeats) {
                    error = true;
                }
                item.setFree(availableSeats);

            } catch (EntryNotFoundException e) {

                // play not found
                iterator.remove();
                error = true;
            }

        }

        // if error -> return the updated cart
        if (error) {
            return Response.status(409).entity(cart.getCart()).build();
        }

        // if here, everything went just fine
        return Response.noContent().build();
    }

}
