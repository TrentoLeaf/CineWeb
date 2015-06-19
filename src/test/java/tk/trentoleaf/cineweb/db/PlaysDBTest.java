package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.exceptions.db.ConstrainException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;
import tk.trentoleaf.cineweb.model.Room;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlaysDBTest extends DBTest {

    @Test
    public void createPlaySuccess() throws Exception {

        // create films, rooms
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);
        final Room r1 = roomsDB.createRoom(23, 12);
        final Room r2 = roomsDB.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(121), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(150), false);

        // insert
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);
        playsDB.createPlay(p3);
        playsDB.createPlay(p4);

        // expected
        final List<Play> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        expected.add(p4);

        // current
        final List<Play> current = playsDB.getPlays();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = ConstrainException.class)
    public void createPlayFailure1() throws Exception {
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        final Play p1 = new Play(f1.getFid(), 223, DateTime.now(), true);
        playsDB.createPlay(p1);
    }

    @Test(expected = ConstrainException.class)
    public void createPlayFailure2() throws Exception {
        final Room r1 = roomsDB.createRoom(23, 12);

        final Play p1 = new Play(23434, r1.getRid(), DateTime.now(), true);
        playsDB.createPlay(p1);
    }

    @Test(expected = AnotherFilmScheduledException.class)
    public void createPlayFailure3() throws Exception {
        final Room r1 = roomsDB.createRoom(23, 12);
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        final Play p1 = new Play(f1.getFid(), r1.getRid(), DateTime.now(), true);

        // ok
        playsDB.createPlay(p1);

        // fail
        playsDB.createPlay(new Play(f1, r1, DateTime.now().plusMinutes(100), false));
    }

    @Test
    public void deletePlaySuccess() throws Exception {

        // create films, rooms
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);
        final Room r1 = roomsDB.createRoom(23, 12);
        final Room r2 = roomsDB.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(121), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(150), false);

        // insert
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);
        playsDB.createPlay(p3);
        playsDB.createPlay(p4);

        // remove 2, 4
        playsDB.deletePlay(p2);
        playsDB.deletePlay(p4);

        // expected
        final List<Play> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p3);

        // current
        final List<Play> current = playsDB.getPlays();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = EntryNotFoundException.class)
    public void deletePlayFailure() throws Exception {
        playsDB.deletePlay(24543);
    }

    @Test
    public void checkPlays() throws Exception {

        final DateTimeFormatter ff = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

        final Film f1 = new Film("Teo", "fantasy", "http://aaa.com", "http://bbb.org", "trama", 60);
        final Film f2 = new Film("Marco", "horror", "http://bbb.com", "http://bbb.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        final Room r1 = roomsDB.createRoom(4, 5);
        final Room r2 = roomsDB.createRoom(2, 3);

        final Play p1 = new Play(f1, r1, ff.parseDateTime("20/05/2015 12:00:00"), true);
        final Play p2 = new Play(f1, r1, ff.parseDateTime("20/05/2015 13:00:01"), true);
        playsDB.createPlay(p1);
        playsDB.createPlay(p2);

        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 11:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 11:59:59")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:02")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 15:00:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:00:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:01:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:10:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 13:00:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 13:00:01")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:01")));

        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 11:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 11:59:59")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 14:00:01")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 15:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 12:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 12:01:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 12:10:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 13:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 13:00:01")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 14:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r2, ff.parseDateTime("20/05/2015 14:00:01")));

        // remove play p1
        playsDB.deletePlay(p1);

        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 11:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 11:59:59")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:02")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 15:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:00:00")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:01:00")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 12:10:00")));
        assertFalse(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 13:00:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 13:00:01")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:00")));
        assertTrue(playsDB.isAlreadyPlay(r1, ff.parseDateTime("20/05/2015 14:00:01")));
    }
}