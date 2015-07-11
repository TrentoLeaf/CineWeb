package tk.trentoleaf.cineweb.rest;

import com.itextpdf.text.DocumentException;
import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.annotations.rest.UserArea;
import tk.trentoleaf.cineweb.beans.model.*;
import tk.trentoleaf.cineweb.beans.rest.in.*;
import tk.trentoleaf.cineweb.db.*;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.exceptions.db.*;
import tk.trentoleaf.cineweb.exceptions.rest.BadRequestException;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Buy procedure end point.
 */
@Path("/buy")
public class RestBuy {

//    private static final int PLAYS_PROBLEMS = 1;
//    private static final int WRONG_CREDITCARD = 2;

    // DB singleton
    private UsersDB usersDB = UsersDB.instance();
    private FilmsDB filmsDB = FilmsDB.instance();
    private PlaysDB playsDB = PlaysDB.instance();
    private RoomsDB roomsDB = RoomsDB.instance();
    private PricesDB pricesDB = PricesDB.instance();
    private BookingsDB bookingsDB = BookingsDB.instance();

    @Context
    private HttpServletRequest request;

    @Context
    private UriInfo uriInfo;

    @POST
    @Path("/proceed")
    public Response checkCart(@NotNull(message = "Missing cart") @Valid CheckCart cart) {

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

    @POST
    @Path("/pay")
    @UserArea
    public Response pay(@NotNull(message = "Missing cart") @Valid PayCart cart) {

        // check PayCart format
        if (!cart.isValidCart()) {
            throw new BadRequestException("Bad cart object: missing or bad selected_seats");
        }

        // check credit card
        if (!cart.isValidCreditCard()) {
            // error credit card!
            throw new BadRequestException("Bad credit card");
        }

        // get current session
        final HttpSession session = request.getSession(false);
        assert session != null;

        // get logged user
        final Integer uid = (Integer) session.getAttribute(Utils.UID);
        assert uid != null;

        // there was something wrong in the cart (eg. old plays etc)
        boolean error = false;

        // create tickets array
        final List<Ticket> tickets = new ArrayList<>();

        // check plays
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
                    continue;
                }

                // check if enough seats
                int availableSeats = roomsDB.freePlacesByPlay(pid);
                int requestedSeats = item.getTotalTickets();

                // if not enough seats -> update free field
                if (availableSeats < requestedSeats) {
                    error = true;
                    item.resetSeats();
                    continue;
                }
                item.setFree(availableSeats);

                // check requested seats -> try to buy (db transaction atomicity)

                // copy requested tickets (prices)
                final List<Price> prices = new ArrayList<>();
                Iterator<TicketItem> ticketItemIterator = item.getTickets().iterator();

                while (ticketItemIterator.hasNext()) {
                    TicketItem t = ticketItemIterator.next();
                    try {
                        Price price = pricesDB.getPrice(t.getType());
                        t.setSingleCost(price.getPrice());
                        for (int i = 0; i < t.getNumber(); i++) {
                            prices.add(price);
                        }
                    } catch (EntryNotFoundException e) {
                        ticketItemIterator.remove();
                        error = true;
                    }
                }

                // if something wrong with prices -> go on...
                if (error) {
                    continue;
                }

                // add tickets
                Iterator<Price> pricesIterator = prices.iterator();
                for (SelectedSeat seat : item.getSelectedSeats()) {
                    Price price = pricesIterator.next();
                    tickets.add(new Ticket(item.getPid(), item.getRid(), seat.getRow(), seat.getCol(),
                            price.getPrice(), price.getType(), false));
                }

            } catch (EntryNotFoundException e) {

                // play not found
                iterator.remove();
                error = true;
            }
        }

        // try to save the booking
        try {
            bookingsDB.createBooking(uid, tickets);

            // get the user
            User user = usersDB.getUser(uid);

            // create tickets data
            List<FilmTicketData> datas = new ArrayList<>();
            for (Ticket t : tickets) {
                Play play = playsDB.getPlay(t.getPid());
                datas.add(new FilmTicketData(t.getTid(), user.getEmail(), filmsDB.getFilm(play.getFid()).getTitle(),
                        t.getRid(), t.getX(), t.getY(), play.getTime(), t.getType(), t.getPrice()));
            }

            // send the email
            EmailSender.instance().sendTicketPDFEmail(uriInfo.getRequestUri(), user, datas);

        } catch (UserNotFoundException | PlayGoneException | EntryNotFoundException | DBException e) {
            error = true;
        } catch (SendGridException | DocumentException e) {
            // TODO!
        }

        // if any error -> return the updated cart
        if (error) {
            return Response.status(409).entity(cart.getCart()).build();
        }

        // if here -> buy OK
        return Response.noContent().build();
    }

}
