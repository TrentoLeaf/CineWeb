package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import tk.trentoleaf.cineweb.exceptions.db.*;
import tk.trentoleaf.cineweb.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BookingsDBTest extends DBTest {

    @Test
    public void createBooking() throws Exception {
        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final List<Seat> s1 = r1.getSeats();
        final List<Seat> s2 = r2.getSeats();

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
        final Play p2 = new Play(f1, r1, ff.parseDateTime("20/10/2015 13:00:01"), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        // create bookings  int rid, int x, int y, int uid, int pid, double price
        final Booking b1 = bookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
        final Booking b2 = bookingsDB.createBooking(s2.get(2).getRid(), s2.get(2).getX(), s2.get(2).getY(), u2.getUid(), p2.getPid(), 12);

        // expected
        List<Booking> expected = new ArrayList<>();
        expected.add(b1);
        expected.add(b2);

        // current
        List<Booking> current = bookingsDB.getBookings();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }


    // booking fail by time expired
    @Test(expected = FilmAlreadyGoneException.class)
    public void createBookingFail() throws Exception {

        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final List<Seat> s1 = r1.getSeats();
        final List<Seat> s2 = r2.getSeats();

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
        final Play p2 = new Play(f1, r1, ff.parseDateTime("20/10/2015 13:00:01"), true);
        final Play p3 = new Play(f1, r1, ff.parseDateTime("20/03/2015 13:00:01"), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);
        playsDB.createPlay(p3);

        // create bookings  int rid, int x, int y, int uid, int pid, double price
        bookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
        bookingsDB.createBooking(s2.get(2).getRid(), s2.get(2).getX(), s2.get(2).getY(), u2.getUid(), p2.getPid(), 12);
        bookingsDB.createBooking(s2.get(3).getRid(), s2.get(3).getX(), s2.get(3).getY(), u2.getUid(), p3.getPid(), 12);
    }

    @Test
    public void deleteBooking() throws Exception {
        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final User u1 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final List<Seat> s1 = r1.getSeats();

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
        playsDB.createPlay(p1);

        // create bookings  int rid, int x, int y, int uid, int pid, double price
        final Booking b1 = bookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
        final Booking b2 = bookingsDB.createBooking(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), u1.getUid(), p1.getPid(), 12);

        // expected
        List<Booking> expected = new ArrayList<>();
        expected.add(b1);
        double creditExpected = u1.getCredit() + b2.getPrice() * 0.8;

        // current
        bookingsDB.deleteBooking(b2.getBid());
        List<Booking> current = bookingsDB.getBookings();
        double creditUpdated = usersDB.getUser(u1.getUid()).getCredit();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
        assertEquals(creditExpected, creditUpdated, 0.00000001);
    }

    @Test(expected = EntryNotFoundException.class)
    public void deleteBookingFail1() throws Exception {
        bookingsDB.deleteBooking(43535);
    }

    @Test(expected = EntryNotFoundException.class)
    public void deleteBookingFail2() throws Exception {
        bookingsDB.deleteBooking(new Booking(345, 4, 6, 4, 345, DateTime.now(), 34.23));
    }

    @Test
    public void getSeatsReservedByPlay() throws Exception {
        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final User u1 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final List<Seat> s1 = r1.getSeats();

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
        playsDB.createPlay(p1);

        // create bookings  int rid, int x, int y, int uid, int pid, double price
        bookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
        bookingsDB.createBooking(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), u1.getUid(), p1.getPid(), 12);

        // expected
        List<SeatReserved> expected = new ArrayList<>();
        expected.add(new SeatReserved(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), true));
        expected.add(new SeatReserved(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), true));

        // current
        List<SeatReserved> current = roomsDB.getSeatsReservedByPlay(p1);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void getSeatsByPlay() throws Exception {
        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final User u1 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
        usersDB.createUser(u1);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        filmsDB.createFilm(f1);

        final Room r1 = roomsDB.createRoom(4, 5);
        final List<Seat> s1 = r1.getSeats();

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
        playsDB.createPlay(p1);

        // create bookings  int rid, int x, int y, int uid, int pid, double price
        bookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
        bookingsDB.createBooking(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), u1.getUid(), p1.getPid(), 12);

        // expected
        List<SeatReserved> expected = new ArrayList<>();
        //expected.add(new SeatReserved(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(),true) );
        //expected.add(new SeatReserved(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(),true) );

        for (Seat seat : s1) {
            expected.add(new SeatReserved(seat.getRid(), seat.getX(), seat.getY(), false));
        }
        expected.remove(new SeatReserved(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), false));
        expected.remove(new SeatReserved(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), false));
        expected.add(new SeatReserved(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), true));
        expected.add(new SeatReserved(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), true));

        // current
        List<SeatReserved> current = roomsDB.getSeatsByPlay(p1);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

}