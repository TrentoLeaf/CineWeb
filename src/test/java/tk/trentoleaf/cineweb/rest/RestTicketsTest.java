package tk.trentoleaf.cineweb.rest;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.*;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestTicketsTest extends MyJerseyTest {

    @Test
    public void deleteTicketSuccess() throws Exception {
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 20);
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().plusMinutes(10), true);
        final Play p2 = new Play(f1, r2, DateTime.now().plusMinutes(200), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 1, 2, 4, "normale");
        final Ticket t2 = new Ticket(p2, 1, 2, 5, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 2, 6, "normale");
        final List<Ticket> tickets = new ArrayList<>();
        tickets.add(t1);
        tickets.add(t2);
        tickets.add(t3);

        // create a booking
        final Booking booking = bookingsDB.createBooking(u1, tickets);

        // login as admin
        final Cookie c = loginAdmin();

        // delete ticket t3
        final Response res1 = getTarget().path("/tickets/" + t3.getTid()).request(JSON).cookie(c).delete();
        assertEquals(204, res1.getStatus());

        // expected bookings
        final List<Booking> expected = new ArrayList<>();
        final List<Ticket> tt = new ArrayList<>();
        tt.add(new Ticket(t1.getTid(), booking.getBid(), p1, 1, 2, 4, "normale"));
        tt.add(new Ticket(t2.getTid(), booking.getBid(), p2, 1, 2, 5, "ridotto"));
        tt.add(new Ticket(t3.getTid(), booking.getBid(), p2, 0, 2, 6, "normale", true));
        final Booking b = new Booking(booking.getBid(), u1, booking.getTime(), 15, tt);
        expected.add(b);

        // current
        List<Booking> current = bookingsDB.getBookingsByUser(u1.getUid());

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteTicketFail1() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 20);
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().plusMinutes(10), true);
        final Play p2 = new Play(f1, r2, DateTime.now().plusMinutes(200), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 1, 2, 4, "normale");
        final Ticket t2 = new Ticket(p2, 1, 2, 5, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 2, 6, "normale");
        final List<Ticket> tickets = new ArrayList<>();
        tickets.add(t1);
        tickets.add(t2);
        tickets.add(t3);

        // create a booking
        final Booking booking = bookingsDB.createBooking(u1, tickets);

        // login as admin
        final Cookie c = loginAdmin();

        // delete ticket t3
        final Response res1 = getTarget().path("/tickets/" + t3.getTid()).request(JSON).cookie(c).delete();
        assertEquals(204, res1.getStatus());

        // 2nd time -> fail
        final Response res2 = getTarget().path("/tickets/" + t3.getTid()).request(JSON).cookie(c).delete();
        assertEquals(404, res2.getStatus());

        // expected bookings
        final List<Booking> expected = new ArrayList<>();
        final List<Ticket> tt = new ArrayList<>();
        tt.add(new Ticket(t1.getTid(), booking.getBid(), p1, 1, 2, 4, "normale"));
        tt.add(new Ticket(t2.getTid(), booking.getBid(), p2, 1, 2, 5, "ridotto"));
        tt.add(new Ticket(t3.getTid(), booking.getBid(), p2, 0, 2, 6, "normale", true));
        final Booking b = new Booking(booking.getBid(), u1, booking.getTime(), 15, tt);
        expected.add(b);

        // current
        List<Booking> current = bookingsDB.getBookingsByUser(u1.getUid());

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteTicketFail2() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // delete non existing ticket
        final Response res1 = getTarget().path("/tickets/" + 345).request(JSON).cookie(c).delete();
        assertEquals(404, res1.getStatus());
    }
}

