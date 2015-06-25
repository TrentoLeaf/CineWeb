package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BookingsDBTest extends DBTest {

    @Test
    public void createBooking() throws Exception {
        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

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

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
        final Play p2 = new Play(f1, r2, ff.parseDateTime("20/10/2015 13:00:01"), true);
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

        // create a booking
        final Booking booking = bookingsDB.createBooking(u1, tickets);

        // expected
        final List<Booking> expected = new ArrayList<>();
        final Booking b = new Booking(booking.getBid(), u1, booking.getTime(), 0, tickets);
        expected.add(b);

        // current
        List<Booking> current = bookingsDB.getBookings(true);

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

//
//    // booking fail by time expired
//    @Test(expected = FilmAlreadyGoneException.class)
//    public void createBookingFail() throws Exception {
//
//        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
//
//        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
//        final User u2 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
//        usersDB.createUser(u1);
//        usersDB.createUser(u2);
//
//        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
//        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
//        filmsDB.createFilm(f1);
//        filmsDB.createFilm(f2);
//
//        final Room r1 = roomsDB.createRoom(4, 5);
//        final Room r2 = roomsDB.createRoom(2, 3);
//
//        final List<Seat> s1 = r1.getSeats();
//        final List<Seat> s2 = r2.getSeats();
//
//        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
//        final Play p2 = new Play(f1, r1, ff.parseDateTime("20/10/2015 13:00:01"), true);
//        final Play p3 = new Play(f1, r1, ff.parseDateTime("20/03/2015 13:00:01"), true);
//        playsDB.createPlay(p1);
//        playsDB.createPlay(p2);
//        playsDB.createPlay(p3);
//
//        // create bookings  int rid, int x, int y, int uid, int pid, double price
//        oldBookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
//        oldBookingsDB.createBooking(s2.get(2).getRid(), s2.get(2).getX(), s2.get(2).getY(), u2.getUid(), p2.getPid(), 12);
//        oldBookingsDB.createBooking(s2.get(3).getRid(), s2.get(3).getX(), s2.get(3).getY(), u2.getUid(), p3.getPid(), 12);
//    }
//
//    @Test
//    public void deleteBooking() throws Exception {
//        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
//
//        final User u1 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
//        usersDB.createUser(u1);
//
//        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
//        filmsDB.createFilm(f1);
//
//        final Room r1 = roomsDB.createRoom(4, 5);
//        final List<Seat> s1 = r1.getSeats();
//
//        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
//        playsDB.createPlay(p1);
//
//        // create bookings  int rid, int x, int y, int uid, int pid, double price
//        final OldBooking b1 = oldBookingsDB.createBooking(s1.get(2).getRid(), s1.get(2).getX(), s1.get(2).getY(), u1.getUid(), p1.getPid(), 12);
//        final OldBooking b2 = oldBookingsDB.createBooking(s1.get(1).getRid(), s1.get(1).getX(), s1.get(1).getY(), u1.getUid(), p1.getPid(), 12);
//
//        // expected
//        List<OldBooking> expected = new ArrayList<>();
//        expected.add(b1);
//        double creditExpected = u1.getCredit() + b2.getPrice() * 0.8;
//
//        // current
//        oldBookingsDB.deleteBooking(b2.getBid());
//        List<OldBooking> current = oldBookingsDB.getBookings();
//        double creditUpdated = usersDB.getUser(u1.getUid()).getCredit();
//
//        // test
//        assertTrue(CollectionUtils.isEqualCollection(expected, current));
//        assertEquals(creditExpected, creditUpdated, 0.00000001);
//    }
//
//    @Test(expected = EntryNotFoundException.class)
//    public void deleteBookingFail1() throws Exception {
//        oldBookingsDB.deleteBooking(43535);
//    }
//
//    @Test(expected = EntryNotFoundException.class)
//    public void deleteBookingFail2() throws Exception {
//        oldBookingsDB.deleteBooking(new OldBooking(345, 4, 6, 4, 345, DateTime.now(), 34.23));
//    }
//
//    @Test
//    public void getSeatsReservedByPlay() throws Exception {
//        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
//
//        final User u1 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
//        usersDB.createUser(u1);
//
//        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
//        filmsDB.createFilm(f1);
//
//        final Room r1 = roomsDB.createRoom(4, 5);
//        final Seat s1 = new Seat(r1.getRid(), 0, 2);
//        final Seat s2 = new Seat(r1.getRid(), 3, 3);
//        final Seat s3 = new Seat(r1.getRid(), 0, 0);
//
//        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
//        playsDB.createPlay(p1);
//
//        // create bookings  int rid, int x, int y, int uid, int pid, double price
//        oldBookingsDB.createBooking(s1, u1, p1, 12.0);
//        oldBookingsDB.createBooking(s2, u1.getUid(), p1.getPid(), 12);
//        oldBookingsDB.createBooking(r1.getRid(), s3.getX(), s3.getY(), u1.getUid(), p1.getPid(), 34);
//
//        // expected
//        List<Seat> expected = new ArrayList<>();
//        expected.add(s1);
//        expected.add(s3);
//        expected.add(s2);
//
//        // current
//        List<Seat> current = roomsDB.getSeatsReservedByPlay(p1);
//
//        // test
//        assertTrue(CollectionUtils.isEqualCollection(expected, current));
//    }
//
//    @Test
//    public void getSeatsByPlay() throws Exception {
//        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
//
//        final User u1 = new User(true, Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz");
//        usersDB.createUser(u1);
//
//        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
//        filmsDB.createFilm(f1);
//
//        final Room r1 = roomsDB.createRoom(4, 5);
//        final Seat s1 = new Seat(r1.getRid(), 0, 2);
//        final Seat s2 = new Seat(r1.getRid(), 3, 3);
//        final Seat s3 = new Seat(r1.getRid(), 0, 0);
//
//        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/10/2015 12:00:00"), true);
//        final Play p2 = new Play(f1, r1, ff.parseDateTime("22/10/2015 15:40:00"), true);
//        playsDB.createPlay(p1);
//        playsDB.createPlay(p2);
//
//        // create bookings
//        oldBookingsDB.createBooking(s1, u1, p1, 12);   // p1
//        oldBookingsDB.createBooking(s2, u1, p1, 12);   // p1
//        oldBookingsDB.createBooking(s3, u1, p2, 12);   // p2
//
//        // expected
//        List<SeatStatus> expected = new ArrayList<>();
//
//        // load all seats
//        for (Seat seat : roomsDB.getSeatsByRoom(r1.getRid())) {
//            expected.add(SeatStatus.fromSeat(seat, false));
//        }
//
//        // remove reserved seats
//        expected.remove(SeatStatus.fromSeat(s1, false));
//        expected.remove(SeatStatus.fromSeat(s2, false));
//
//        // add reserved seats as reserved
//        expected.add(SeatStatus.fromSeat(s1, true));
//        expected.add(SeatStatus.fromSeat(s2, true));
//
//        // current
//        List<SeatStatus> current = roomsDB.getSeatsByPlay(p1);
//
//        // test
//        assertTrue(CollectionUtils.isEqualCollection(expected, current));
//    }

}