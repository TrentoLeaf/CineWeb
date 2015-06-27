package tk.trentoleaf.cineweb.db;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.Film;
import tk.trentoleaf.cineweb.beans.model.Play;
import tk.trentoleaf.cineweb.beans.model.Room;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.exceptions.db.ForeignKeyException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class FilmsDBTest extends DBTest {

    @Test
    public void insertFilmSuccess() throws Exception {

        // films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);

        // save films
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f1);
        expected.add(f2);

        // current
        List<Film> current = filmsDB.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteFilmSuccess1() throws Exception {

        // save films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        // delete
        filmsDB.deleteFilm(f1.getFid());

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f2);

        // current
        List<Film> current = filmsDB.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void deleteFilmSuccess2() throws Exception {

        // save films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        filmsDB.createFilm(f2);
        filmsDB.createFilm(f1);

        // delete
        filmsDB.deleteFilm(f2);

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f1);

        // current
        List<Film> current = filmsDB.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }


    @Test(expected = EntryNotFoundException.class)
    public void deleteFilmFail() throws Exception {

        // delete
        filmsDB.deleteFilm(43543543);
    }

    @Test(expected = ForeignKeyException.class)
    public void deleteFilmFail2() throws Exception {

        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room r1 = roomsDB.createRoom(1, 2);

        playsDB.createPlay(new Play(f1, r1, DateTime.now(), false));

        // should fail
        filmsDB.deleteFilm(f1);
    }

    @Test
    public void updateFilmSuccess() throws Exception {

        // save films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        filmsDB.createFilm(f1);
        filmsDB.createFilm(f2);

        // edit film 2
        f2.setTitle("New title");
        f2.setGenre("Wowo");
        f2.setTrailer(null);
        f2.setPlaybill("http:////");
        f2.setPlot(null);
        f2.setDuration(33);

        // update
        filmsDB.updateFilm(f2);

        // expected
        List<Film> expected = new ArrayList<>();
        expected.add(f2);
        expected.add(f1);

        // current
        List<Film> current = filmsDB.getFilms();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test(expected = EntryNotFoundException.class)
    public void updateFilmFail() throws Exception {

        // film
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // edit
        filmsDB.updateFilm(f1);
    }

}