package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class StatisticsDBTest extends DBTest {

    @Test
    public void getFilmsGrossing() throws Exception {

        final User u1 = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        usersDB.createUser(u1);
        final User u2 = new User(true, Role.CLIENT, "aaa@teo.com", "aaa", "aaa", "aaa", 100);
        usersDB.createUser(u2);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        final Film f3 = new Film("Pippo", "pluto", "http://bbb.com", "http://bbb.org", "trama", 234);
        final Film f4 = new Film("X", "a", "http://bbb.com", "http://bbb.org", "trama", 3);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);
        filmsDB.createFilm(f3);
        filmsDB.createFilm(f4);

        final Room r1 = roomsDB.createRoom(5, 5);
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
        final Ticket t7 = new Ticket(p5, 1, 1, 20, "ridotto");
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

        // expected
        final FilmGrossing g1 = new FilmGrossing(f1.getFid(), f1.getTitle(), 35);
        final FilmGrossing g2 = new FilmGrossing(f2.getFid(), f2.getTitle(), 37.5);
        final FilmGrossing g3 = new FilmGrossing(f3.getFid(), f3.getTitle(), 23);
        final FilmGrossing g4 = new FilmGrossing(f4.getFid(), f4.getTitle(), 0);
        final List<FilmGrossing> expected = Arrays.asList(g1, g2, g3, g4);

        // current
        final List<FilmGrossing> current = statisticsDB.getFilmsGrossing();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void getTopClients() throws Exception {

        final User u1 = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni", 0);
        usersDB.createUser(u1);
        final User u2 = new User(true, Role.CLIENT, "aaa@teo.com", "aaa", "aaa", "aaa", 100);
        usersDB.createUser(u2);
        final User u3 = new User(true, Role.CLIENT, "bbb@teo.com", "bbb", "bbb", "bbb", 200);
        usersDB.createUser(u3);

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        final Film f3 = new Film("Pippo", "pluto", "http://bbb.com", "http://bbb.org", "trama", 234);
        final Film f4 = new Film("X", "a", "http://bbb.com", "http://bbb.org", "trama", 3);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);
        filmsDB.createFilm(f3);
        filmsDB.createFilm(f4);

        final Room r1 = roomsDB.createRoom(5, 5);
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

        // expected
        final TopClient c1 = new TopClient(u1.getUid(), u1.getFirstName(), u1.getSecondName(), 4, 42.5);
        final TopClient c2 = new TopClient(u2.getUid(), u2.getFirstName(), u2.getSecondName(), 5, 35);
        final List<TopClient> expected = Arrays.asList(c1, c2);

        // current
        final List<TopClient> current = statisticsDB.getTopClients();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

}