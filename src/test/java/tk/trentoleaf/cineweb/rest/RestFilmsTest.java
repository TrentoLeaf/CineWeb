package tk.trentoleaf.cineweb.rest;

import org.junit.Test;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

        // update user
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

        // update user
        final Response r1 = getTarget().path("/films/" + 343).request(JSON).cookie(c).put(Entity.json(f2));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void editFilmFail2() throws Exception {

        // new film
        final Film f2 = new Film("Davide amicone", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);

        // no login

        // update user
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

        // update user
        final Response r1 = getTarget().path("/films/" + f1.getFid()).request(JSON).cookie(c).put(Entity.json(f2));
        assertEquals(400, r1.getStatus());
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

}

