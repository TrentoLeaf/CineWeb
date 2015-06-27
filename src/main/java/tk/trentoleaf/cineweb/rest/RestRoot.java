package tk.trentoleaf.cineweb.rest;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.annotations.rest.AdminArea;
import tk.trentoleaf.cineweb.beans.model.*;
import tk.trentoleaf.cineweb.db.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public void loadExampleData() throws Exception {

        // TODO!

        // films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        final Film f3 = new Film("Titolo 1", "Genere 1", "http://bbb.com", "../webapp/img/temporary/img1.jpg", "Descrizione 1", 123);
        final Film f4 = new Film("Titolo 2", "Genere 2", "http://asdf.com", "../webapp/img/temporary/img2.jpg", "Descrizione 2", 234);
        final Film f5 = new Film("Titolo 3", "Genere 3", "http://asdfasdf.com", "../webapp/img/temporary/img3.jpg", "Descrizione 3", 233);
        final Film f6 = new Film("Titolo 4", "Genere 4", "http://asdfdsa.it", "../webapp/img/temporary/img4.jpg", "Descrizione 4", 332);
        final Film f7 = new Film("Titolo 5", "Genere 5", "http://asdfdds.org", "../webapp/img/temporary/img5.jpg", "Descrizione 5", 331);
        final Film f8 = new Film("Titolo 6", "Genere 6", "asdfdsafd", "../webapp/img/temporary/img6.jpg", "Descrizione 6", 555);
        final Film f9 = new Film("Titolo 7", "Genere 7", "asdfFDsa", "../webapp/img/temporary/img7.jpg", "Descrizione 7", 322);
        final Film f10 = new Film("Titolo 8", "Genere 8", "Asdfds", "../webapp/img/temporary/img8.jpg", "Descrizione 8", 221);
        final Film f11 = new Film("Titolo 9", "Genere 9", "asdffdd", "../webapp/img/temporary/img9.jpg", "Descrizione 9", 227);

        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);
        filmsDB.createFilm(f3);
        filmsDB.createFilm(f4);
        filmsDB.createFilm(f5);
        filmsDB.createFilm(f6);
        filmsDB.createFilm(f7);
        filmsDB.createFilm(f8);
        filmsDB.createFilm(f9);
        filmsDB.createFilm(f10);
        filmsDB.createFilm(f11);

        // rooms
        final Room r1 = roomsDB.createRoom(23, 12);
        final Room r2 = roomsDB.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(1000), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2000), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(3000), false);
        final Play p5 = new Play(f3, r1, DateTime.now().plusMinutes(4000), false);
        final Play p6 = new Play(f4, r1, DateTime.now().plusMinutes(5000), false);
        final Play p7 = new Play(f5, r1, DateTime.now().plusMinutes(6000), false);
        final Play p8 = new Play(f2, r1, DateTime.now().plusMinutes(7000), false);
        final Play p9 = new Play(f2, r1, DateTime.now().plusMinutes(8000), false);
        final Play p10 = new Play(f2, r1, DateTime.now().plusMinutes(9000), false);
        final Play p11 = new Play(f2, r1, DateTime.now().plusMinutes(10000), false);

        // insert
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);
        playsDB.createPlay(p3);
        playsDB.createPlay(p4);
        playsDB.createPlay(p5);
        playsDB.createPlay(p6);
        playsDB.createPlay(p7);
        playsDB.createPlay(p8);
        playsDB.createPlay(p9);
        playsDB.createPlay(p10);
        playsDB.createPlay(p11);

    }
}
