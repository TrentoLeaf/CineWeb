package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.*;
import tk.trentoleaf.cineweb.exceptions.db.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookingsDBTest extends DBTest {

    @Test
    public void createBookingSuccess1() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz", 0);
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().plusMinutes(10), true);
        final Play p2 = new Play(f1, r2, DateTime.now().plusMinutes(120), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 1, 2, 9.33, "normale");
        final Ticket t2 = new Ticket(p2, 1, 2, 8.50, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 2, 8.50, "normale");
        final List<Ticket> tickets = new ArrayList<>();
        tickets.add(t1);
        tickets.add(t2);
        tickets.add(t3);
        t1.setTitle(f1.getTitle());
        t2.setTitle(f1.getTitle());
        t3.setTitle(f1.getTitle());
        t1.setTime(p1.getTime());
        t2.setTime(p2.getTime());
        t3.setTime(p2.getTime());

        // create a booking
        final Booking booking = bookingsDB.createBooking(u1, tickets);

        // check user credit
        assertEquals(0, booking.getPayedWithCredit(), 0);
        assertEquals(u1, usersDB.getUser(u1.getUid()));

        // expected
        final List<Booking> expected = new ArrayList<>();
        final Booking b = new Booking(booking.getBid(), u1, booking.getTime(), 0, tickets);
        expected.add(b);

        // current
        List<Booking> current = bookingsDB.getBookings();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void createBookingSuccess2() throws Exception {

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
        t1.setTitle(f1.getTitle());
        t2.setTitle(f1.getTitle());
        t3.setTitle(f1.getTitle());
        t1.setTime(p1.getTime());
        t2.setTime(p2.getTime());
        t3.setTime(p2.getTime());

        // create a booking
        final Booking booking = bookingsDB.createBooking(u1, tickets);

        // check user credit
        assertEquals(15, booking.getPayedWithCredit(), 0);
        u1.setCredit(5);
        assertEquals(u1, usersDB.getUser(u1.getUid()));

        // expected bookings
        final List<Booking> expected = new ArrayList<>();
        final Ticket tt1 = new Ticket(t1.getTid(), booking.getBid(), p1, 1, 2, 4, "normale");
        final Ticket tt2 = new Ticket(t3.getTid(), booking.getBid(), p2, 0, 2, 6, "normale");
        final Ticket tt3 = new Ticket(t2.getTid(), booking.getBid(), p2, 1, 2, 5, "ridotto");
        tt1.setTitle(f1.getTitle());
        tt2.setTitle(f1.getTitle());
        tt3.setTitle(f1.getTitle());
        tt1.setTime(p1.getTime());
        tt2.setTime(p2.getTime());
        tt3.setTime(p2.getTime());

        final Booking b = new Booking(booking.getBid(), u1, booking.getTime(), 15, Arrays.asList(tt1, tt2, tt3));
        expected.add(b);

        // current
        List<Booking> current = bookingsDB.getBookingsByUser(u1.getUid());

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = UniqueViolationException.class)
    public void createBookingFail1() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz", 0);
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().plusMinutes(10), true);
        final Play p2 = new Play(f1, r2, DateTime.now().plusMinutes(200), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 1, 2, 9.33, "normale");
        final Ticket t2 = new Ticket(p2, 1, 2, 8.50, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 2, 8.50, "normale");
        final List<Ticket> tt1 = new ArrayList<>();
        tt1.add(t1);
        tt1.add(t2);
        tt1.add(t3);

        // create a booking -> OK
        bookingsDB.createBooking(u1, tt1);

        // create a booking -> fail -> seat reserved!
        final Ticket t4 = new Ticket(p1, 1, 2, 45, "aaa");
        final List<Ticket> tt2 = new ArrayList<>();
        tt2.add(t4);

        // create booking -> fail
        bookingsDB.createBooking(u2, tt2);
    }

    @Test(expected = UserNotFoundException.class)
    public void createBookingFail2() throws Exception {

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().plusMinutes(10), true);
        final Play p2 = new Play(f1, r2, DateTime.now().plusMinutes(200), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 1, 2, 9.33, "normale");
        final Ticket t2 = new Ticket(p2, 1, 2, 8.50, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 2, 8.50, "normale");
        final List<Ticket> tt1 = new ArrayList<>();
        tt1.add(t1);
        tt1.add(t2);
        tt1.add(t3);

        // create a booking -> no user!
        bookingsDB.createBooking(45, tt1);
    }

    @Test(expected = PlayGoneException.class)
    public void createBookingFail3() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().minusMinutes(2), true);
        final Play p2 = new Play(f1, r2, DateTime.now().plusMinutes(200), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // tickets to buy
        final Ticket t1 = new Ticket(p1, 1, 2, 9.33, "normale");
        final Ticket t2 = new Ticket(p2, 1, 2, 8.50, "ridotto");
        final Ticket t3 = new Ticket(p2, 0, 2, 8.50, "normale");
        final List<Ticket> tt1 = new ArrayList<>();
        tt1.add(t1);
        tt1.add(t2);
        tt1.add(t3);

        // create a booking -> a play is already gone...
        bookingsDB.createBooking(u1, tt1);
    }

    @Test(expected = ForeignKeyException.class)
    public void createBookingFail4() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, DateTime.now().plusMinutes(10), true);
        playsDB.createPlay(p1);

        // tickets to buy
        final Ticket t1 = new Ticket(p1.getPid(), r2.getRid(), 1, 2, 9.33, "normale", false);
        final List<Ticket> tt1 = new ArrayList<>();
        tt1.add(t1);

        // create a booking -> fails... wrong rid (p1 is in r1, requested r2)
        bookingsDB.createBooking(u1, tt1);
    }

    @Test
    public void deleteTicketSuccess() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
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
        t1.setTitle(f1.getTitle());
        t2.setTitle(f1.getTitle());
        t3.setTitle(f1.getTitle());
        t1.setTime(p1.getTime());
        t2.setTime(p2.getTime());
        t3.setTime(p2.getTime());

        // create a booking
        final Booking booking = bookingsDB.createBooking(u1, tickets);

        // try to delete the second ticket
        bookingsDB.deleteTicket(t2);

        // check deletion
        t2.setDeleted(true);
        List<Booking> expected = new ArrayList<>();
        expected.add(booking);
        assertTrue(CollectionUtils.isEqualCollection(expected, bookingsDB.getBookings()));

        // check user credit
        u1.addCredit(t2.getPrice() * 0.80);
        assertEquals(u1, usersDB.getUser(u1.getUid()));
    }

    @Test(expected = EntryNotFoundException.class)
    public void deleteTicketFail() throws Exception {
        bookingsDB.deleteTicket(34);
    }

}