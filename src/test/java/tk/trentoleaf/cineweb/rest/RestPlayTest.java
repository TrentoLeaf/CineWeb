package tk.trentoleaf.cineweb.rest;

import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;
import tk.trentoleaf.cineweb.model.Room;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestPlayTest extends MyJerseyTest {

    @Test
    public void getPlaySuccess() throws Exception {

        // create a play
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);
        final Play p1 = new Play(f1, room, DateTime.now(), true);
        playsDB.createPlay(p1);

        // no need to login

        // get play by id
        final Response r1 = getTarget().path("/plays/" + f1.getFid()).request(JSON).get();
        assertEquals(200, r1.getStatus());
        assertEquals(p1, r1.readEntity(Play.class));
    }

    @Test
    public void getPlayFail() throws Exception {

        // get film by id
        final Response r1 = getTarget().path("/plays/" + 98798).request(JSON).get();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void getPlays() throws Exception {

        // create a play
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);
        final Play p1 = new Play(f1, room, DateTime.now(), true);
        playsDB.createPlay(p1);

        // list of films
        final List<Play> plays = new ArrayList<>();
        plays.add(p1);

        // get films
        final Response r1 = getTarget().path("/plays").request(JSON).get();
        assertEquals(200, r1.getStatus());
        assertEquals(plays, r1.readEntity(new GenericType<List<Play>>() {
        }));
    }

    @Test
    public void addPlaySuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // film and room
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);

        // play to create
        final Play p1 = new Play(f1, room, DateTime.now(), true);

        // create film
        final Response r1 = getTarget().path("/plays").request(JSON).cookie(c).post(Entity.json(p1));
        assertEquals(200, r1.getStatus());

        // get created play
        final Play created = r1.readEntity(Play.class);
        p1.setPid(created.getPid());

        // compare
        assertEquals(p1, created);
    }

    @Test
    public void addPlayFail1() throws Exception {

        // login as admin
        final Cookie c = loginClient();

        // film and room
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);

        // play to create
        final Play p1 = new Play(f1, room, DateTime.now(), true);

        // create film
        final Response r1 = getTarget().path("/plays").request(JSON).cookie(c).post(Entity.json(p1));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void addPlayFail2() throws Exception {

        // no login

        // film and room
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);

        // play to create
        final Play p1 = new Play(f1, room, DateTime.now(), true);

        // create film
        final Response r1 = getTarget().path("/plays").request(JSON).post(Entity.json(p1));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void addPlayFail3() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // film and room
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        roomsDB.createRoom(3, 4);

        // play to create
        final Play p1 = new Play(f1.getFid(), 0, DateTime.now(), true);

        // create film
        final Response r1 = getTarget().path("/plays").request(JSON).cookie(c).post(Entity.json(p1));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void addPlayFail4() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // film and room
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);

        // play to create
        final Play p1 = new Play(f1, room, DateTime.now(), true);
        playsDB.createPlay(p1);
        final Play p2 = new Play(f1, room, DateTime.now().plusMinutes(40), true);

        // create film
        final Response r1 = getTarget().path("/plays").request(JSON).cookie(c).post(Entity.json(p2));
        assertEquals(409, r1.getStatus());
    }

    @Test(expected = EntryNotFoundException.class)
    public void deletePlaySuccess() throws Exception {

        // film and room and play
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);
        final Play p1 = new Play(f1, room, DateTime.now(), true);
        playsDB.createPlay(p1);

        // login as admin
        final Cookie c = loginAdmin();

        // delete user
        final Response r1 = getTarget().path("/plays/" + p1.getPid()).request(JSON).cookie(c).delete();
        assertEquals(200, r1.getStatus());

        // should throw an exception
        playsDB.getPlay(p1.getPid());
    }

    @Test
    public void deletePlayFail1() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // delete user
        final Response r1 = getTarget().path("/plays/" + 234).request(JSON).cookie(c).delete();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void deletePlayFail2() throws Exception {

        // film and room and play
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);
        final Play p1 = new Play(f1, room, DateTime.now(), true);
        playsDB.createPlay(p1);

        // login as client
        final Cookie c = loginClient();

        // delete user
        final Response r1 = getTarget().path("/plays/" + p1.getPid()).request(JSON).cookie(c).delete();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void deletePlayFail3() throws Exception {

        // film and room and play
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        filmsDB.createFilm(f1);
        final Room room = roomsDB.createRoom(3, 4);
        final Play p1 = new Play(f1, room, DateTime.now(), true);
        playsDB.createPlay(p1);

        // no login

        // delete user
        final Response r1 = getTarget().path("/films/" + p1.getPid()).request(JSON).delete();
        assertEquals(401, r1.getStatus());
    }

}

