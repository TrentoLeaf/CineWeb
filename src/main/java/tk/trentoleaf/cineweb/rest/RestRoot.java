package tk.trentoleaf.cineweb.rest;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.beans.model.*;
import tk.trentoleaf.cineweb.db.*;
import tk.trentoleaf.cineweb.utils.ExampleData;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

// TODO

/**
 * Resources root.
 */
@Path("/")
public class RestRoot {

    // db singleton
    private UsersDB usersDB = UsersDB.instance();
    private FilmsDB filmsDB = FilmsDB.instance();
    private RoomsDB roomsDB = RoomsDB.instance();
    private PlaysDB playsDB = PlaysDB.instance();
    private BookingsDB bookingsDB = BookingsDB.instance();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "Jersey Working!";
    }

    ////////////////////////////////////////////////////////////////////////////
    // TODO: remove!!!
    ////////////////////////////////////////////////////////////////////////////

    @POST
    @Path("/reset")
    @AdminArea
    public synchronized void test() throws Exception {

        // re-init db
        DB.instance().reset();
        DB.instance().init();
        UsersDB.instance().createAdminUser();
        PricesDB.instance().loadDefaultPrices();

        final User u1 = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        usersDB.createUser(u1);
        final User u2 = new User(true, Role.CLIENT, "aaa@teo.com", "aaa", "aaa", "aaa", 100);
        usersDB.createUser(u2);
        final User u3 = new User(true, Role.CLIENT, "bbb@teo.com", "bbb", "bbb", "bbb", 200);
        usersDB.createUser(u3);

        final Film f1 = new Film("Teo", "fantasy", "https://www.youtube.com/embed/M7lc1UVf-VE", "http://ecx.images-amazon.com/images/I/71XRbg2VHlL._SL1024_.jpg", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "https://www.youtube.com/embed/M7lc1UVf-VE", "http://ecx.images-amazon.com/images/I/81qcjVqV3GL._SL1500_.jpg", "trama", 30);
        final Film f3 = new Film("Pippo", "pluto", "https://www.youtube.com/embed/M7lc1UVf-VE", "http://ecx.images-amazon.com/images/I/512BKFK7xlL.jpg", "trama", 234);
        final Film f4 = new Film("X", "a", "https://www.youtube.com/embed/M7lc1UVf-VE", "http://ecx.images-amazon.com/images/I/61UzrSu0kcL.jpg", "trama", 3);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);
        filmsDB.createFilm(f3);
        filmsDB.createFilm(f4);

        final Room r1 = roomsDB.createRoom(5, 5, Arrays.asList(new Seat(1, 0), new Seat(0, 1)));
        final Room r2 = roomsDB.createRoom(6, 6);

        final Play p1 = new Play(f1, r1, DateTime.now().plusDays(1), true);
        final Play p2 = new Play(f1, r1, DateTime.now().plusDays(2), false);
        final Play p3 = new Play(f4, r2, DateTime.now().plusDays(3), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusDays(4), false);
        final Play p5 = new Play(f2, r1, DateTime.now().plusDays(5), false);
        final Play p6 = new Play(f3, r2, DateTime.now().plusDays(6), false);
        final Play p7 = new Play(f3, r2, DateTime.now().plusDays(7), false);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);
        playsDB.createPlay(p3);
        playsDB.createPlay(p4);
        playsDB.createPlay(p5);
        playsDB.createPlay(p6);
        playsDB.createPlay(p7);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 0, 0, 10, "normale");
        final Ticket t2 = new Ticket(p1, 1, 1, 20, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 0, 5, "normale");
        final Ticket t4 = new Ticket(p6, 3, 3, 55, "normale");
        final Ticket t5 = new Ticket(p4, 3, 4, 7.5, "normale");
        final List<Ticket> tt1 = Arrays.asList(t1, t2, t3, t4, t5);

        final Ticket t6 = new Ticket(p5, 0, 0, 10, "normale");
        final Ticket t7 = new Ticket(p5, 1, 1, 2, "ridotto");
        final Ticket t8 = new Ticket(p6, 0, 0, 5, "normale");
        final List<Ticket> tt2 = Arrays.asList(t6, t7, t8);

        final Ticket t9 = new Ticket(p6, 3, 4, 15, "normale");
        final Ticket t10 = new Ticket(p7, 3, 4, 3, "normale");
        final List<Ticket> tt3 = Arrays.asList(t9, t10);

        // create a booking
        bookingsDB.createBooking(u1, tt1);
        bookingsDB.createBooking(u2, tt2);
        bookingsDB.createBooking(u2, tt3);

        // delete some tickets
        bookingsDB.deleteTicket(t4);
    }

    @POST
    @Path("/load-example-data")
    @AdminArea
    public synchronized Response loadExampleData() {
        try {

            int x = 100;

            // re-init db
            DB.instance().reset();
            DB.instance().init();
            UsersDB.instance().createAdminUser();
            PricesDB.instance().loadDefaultPrices();

            // load example data
            ExampleData.instance().loadExampleData(x);

            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();

            return Response.status(500).build();



        }
    }
}
