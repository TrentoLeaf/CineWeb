package tk.trentoleaf.cineweb.rest;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.*;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestFilmsTest extends MyJerseyTest {

    @Test
    public void getFilmSuccess() throws Exception {

        // create a film
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        // no need to login

        // get film by id
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).get();
        assertEquals(200, r1.getStatus());
        assertEquals(f1, r1.readEntity(Film.class));
    }

    @Test
    public void getFilmFail() throws Exception {

        // get film by id
        final Response r1 = getTarget().path("/films/" + 98798).request(JSON).get();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void getFilms() throws Exception {

        // create a film
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        // list of films
        final List<Film> films = new ArrayList<>();
        films.add(f1);

        // get films
        final Response r1 = getTarget().path("/films").request(JSON).get();
        assertEquals(200, r1.getStatus());
        assertEquals(films, r1.readEntity(new GenericType<List<Film>>() {
        }));

        // create film 2
        final Film f2 = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f2);
        films.add(f2);

        // get films
        final Response r2 = getTarget().path("/films").request(JSON).get();
        assertEquals(200, r2.getStatus());
        assertEquals(films, r2.readEntity(new GenericType<List<Film>>() {
        }));
    }

    @Test
    public void addFilmSuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // film to create
        final Film toCreate = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // create film
        final Response r1 = getTarget().path("/films").request(JSON).cookie(c).post(Entity.json(toCreate));
        assertEquals(200, r1.getStatus());

        // get created film
        final Film created = r1.readEntity(Film.class);
        toCreate.setFid(created.getFid());

        // compare
        assertEquals(toCreate, created);
    }

    @Test
    public void addFilmFail1() throws Exception {

        // login as admin
        final Cookie c = loginClient();

        // film to create
        final Film toCreate = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // create film
        final Response r1 = getTarget().path("/films").request(JSON).cookie(c).post(Entity.json(toCreate));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void addFilmFail2() throws Exception {

        // no login

        // film to create
        final Film toCreate = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // create film
        final Response r1 = getTarget().path("/films").request(JSON).post(Entity.json(toCreate));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void addFilmFail3() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // film to create
        // bad film
        final Film toCreate = new Film(null, "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // create film
        final Response r1 = getTarget().path("/films").request(JSON).cookie(c).post(Entity.json(toCreate));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void addFilmFail4() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // no film provided
        // create film
        final Response r1 = getTarget().path("/films").request(JSON).cookie(c).post(null);
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void editFilmSuccess() throws Exception {

        // create film in filmsDB
        final Film f1 = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Film f2 = new Film("Davide amicone", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // login as admin
        final Cookie c = loginAdmin();

        // update film
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).cookie(c).put(Entity.json(f2));
        assertEquals(200, r1.getStatus());

        f2.setFid(f1.getFid());
        assertEquals(filmsDB.getFilm(f1.getFid()), f2);
    }

    @Test
    public void editFilmFail1() throws Exception {

        // new film
        final Film f2 = new Film("Davide amicone", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // login as client
        final Cookie c = loginClient();

        // update film
        final Response r1 = getTarget().path("/films/" + 343).request(JSON).cookie(c).put(Entity.json(f2));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void editFilmFail2() throws Exception {

        // new film
        final Film f2 = new Film("Davide amicone", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // no login

        // update film
        final Response r1 = getTarget().path("/films/" + 343).request(JSON).put(Entity.json(f2));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void editFilmFail3() throws Exception {

        // create film in filmsDB
        final Film f1 = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Film f2 = new Film("Davide amicone", "sfdsf", "not an url", "http://aaaa.org", "trama moltooo lunga", 120);

        // login as admin
        final Cookie c = loginAdmin();

        // update film
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).cookie(c).put(Entity.json(f2));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void editFilmFail4() throws Exception {

        // film to edit
        final Film film = new Film("Davide amicone", "sfdsf", "http://www.url.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // login as admin
        final Cookie c = loginAdmin();

        // update film -> should fail (not found)
        final Response r1 = getTarget().path("/films/" + 453).request(JSON).cookie(c).put(Entity.json(film));
        assertEquals(404, r1.getStatus());
    }

    @Test(expected = EntryNotFoundException.class)
    public void deleteFilmSuccess() throws Exception {

        // create film to delete in filmsDB
        final Film f1 = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        // login as admin
        final Cookie c = loginAdmin();

        // delete user
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).cookie(c).delete();
        assertEquals(200, r1.getStatus());

        // should throw an exception
        filmsDB.getFilm(f1.getFid());
    }

    @Test
    public void deleteFilmFail1() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // delete user
        final Response r1 = getTarget().path("/films/" + 234).request(JSON).cookie(c).delete();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void deleteFilmFail2() throws Exception {

        // create film to delete in filmsDB
        final Film f1 = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        // login as client
        final Cookie c = loginClient();

        // delete user
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).cookie(c).delete();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void deleteFilmFail3() throws Exception {

        // create film to delete in filmsDB
        final Film f1 = new Film("sdofijoisdf", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);

        // no login

        // delete user
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).delete();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void getGrossingSuccess() throws Exception {

        // fill db
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

        // login as admin
        final Cookie c = loginAdmin();

        // get data
        final Response response = getTarget().path("/films/grossing").request(JSON).cookie(c).get();
        assertEquals(200, response.getStatus());

        // current
        final List<FilmGrossing> current = response.readEntity(new GenericType<List<FilmGrossing>>() {
        });

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void getGrossingFail1() throws Exception {

        // client login
        final Cookie c = loginClient();

        // get data
        final Response r1 = getTarget().path("/films/grossing").request(JSON).cookie(c).get();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void getGrossingFail2() throws Exception {

        // no login

        // get data
        final Response r1 = getTarget().path("/films/grossing").request(JSON).get();
        assertEquals(401, r1.getStatus());
    }

}

