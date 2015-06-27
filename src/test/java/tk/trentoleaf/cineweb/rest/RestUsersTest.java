package tk.trentoleaf.cineweb.rest;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestUsersTest extends MyJerseyTest {

    @Test
    public void getUserSuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // create a user
        final User u = new User(true, Role.CLIENT, "email@email.com", "pass", "name", "name", 34.5);
        usersDB.createUser(u);

        // search the user
        final Response r1 = getTarget().path("/users/" + u.getUid()).request(JSON).cookie(c).get();
        assertEquals(200, r1.getStatus());
        assertEquals(u, r1.readEntity(User.class));
    }

    @Test
    public void getUserFail1() throws Exception {

        // create a user
        final User u = new User(true, Role.CLIENT, "email@email.com", "pass", "name", "name");
        usersDB.createUser(u);

        // no admin
        final Response r1 = getTarget().path("/users/" + u.getUid()).request(JSON).get();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void getUserFail2() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // no user found
        final Response r1 = getTarget().path("/users/" + 34).request(JSON).cookie(c).get();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void getUsersSuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        final Response response = getTarget().path("/users").request(JSON).cookie(c).get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getUsersFail1() throws Exception {

        // FAIL -> not logged as ADMIN
        final Response response = getTarget().path("/users").request(JSON).get();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void getUsersFail2() throws Exception {

        // FAIL -> logged as CLIENT
        final Cookie c = loginClient();

        final Response response = getTarget().path("/users").request(JSON).cookie(c).get();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void createUserSuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // create a user
        final Response response = getTarget().path("/users/").request(JSON).cookie(c)
                .post(Entity.json(new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni")));
        assertEquals(200, response.getStatus());

        // test
        final User expected = response.readEntity(User.class);

        // current
        final User current = usersDB.getUser(expected.getUid());

        // test
        assertEquals(expected, current);
    }

    @Test
    public void createUserFail1() throws Exception {

        // login as client
        final Cookie c = loginClient();

        // create a user -> DENIED (client)
        final Response response = getTarget().path("/users/").request(JSON).cookie(c)
                .post(Entity.json(new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni")));
        assertEquals(401, response.getStatus());
    }

    @Test
    public void createUserFail2() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // create a user
        final Response r1 = getTarget().path("/users/").request(JSON).cookie(c)
                .post(Entity.json(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni")));
        assertEquals(200, r1.getStatus());

        // create a user -_> should fail
        final Response r2 = getTarget().path("/users/").request(JSON).cookie(c)
                .post(Entity.json(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni")));
        assertEquals(409, r2.getStatus());
    }

    @Test
    public void createUserFail3() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // create a user -> bad request
        final Response response = getTarget().path("/users/").request(JSON).cookie(c)
                .post(Entity.json(new User(true, Role.ADMIN, "sdf2", null, "Matteo", "Zeni")));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void createUserFail4() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // create a user
        final Response response = getTarget().path("/users/").request(JSON).cookie(c)
                .post(Entity.json(new User(true, Role.CLIENT, "teo@teo.com", null, "Matteo", "Zeni")));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void updateUserSuccess() throws Exception {

        // login as ADMIN
        final Cookie c = loginAdmin();

        // user to update
        final User u1 = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // try update
        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(JSON)
                .cookie(c).put(Entity.json(new User(true, Role.CLIENT, "a@a.com", null, "Matteo", "Zeni")));
        assertEquals(200, r1.getStatus());

        // check
        final User current = usersDB.getUser("a@a.com");
        final User expected = new User(true, Role.CLIENT, "a@a.com", null, "Matteo", "Zeni");
        expected.setUid(current.getUid());
        assertEquals(expected, current);
    }

    @Test
    public void updateUserFail1() throws Exception {

        // login as CLIENT
        final Cookie c = loginClient();

        // user to update
        final User u1 = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // try update
        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(JSON)
                .cookie(c).put(Entity.json(new User(true, Role.CLIENT, "a@a.com", null, "Matteo", "Zeni")));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void updateUserFail2() throws Exception {

        // user to update
        final User u1 = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // try update (FAIL - no session)
        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(JSON)
                .put(Entity.json(new User(true, Role.CLIENT, "a@a.com", null, "Matteo", "Zeni")));
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void updateUserFail3() throws Exception {

        // wrong path1
        final Response response = getTarget().path("/users/").request(JSON)
                .put(Entity.json(new User(true, Role.ADMIN, "sdf2", null, "Matteo", "Zeni")));
        assertEquals(405, response.getStatus());
    }

    @Test
    public void updateUserFail4() throws Exception {

        // login as ADMIN
        final Cookie c = loginAdmin();

        // user not found
        final Response r1 = getTarget().path("/users/" + 2345).request(JSON)
                .cookie(c).put(Entity.json(new User(true, Role.ADMIN, "sdf2@aaa.com", null, "Matteo", "Zeni")));
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void updateUserFail5() throws Exception {

        // login as ADMIN
        final Cookie c = loginAdmin();

        // user to edit
        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u1);

        // bad request
        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(JSON)
                .cookie(c).put(Entity.json(new User(true, Role.ADMIN, "sdf2", null, null, "Zeni")));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void updateUserFail6() throws Exception {

        // login as ADMIN
        final Cookie c = loginAdmin();

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.ADMIN, "aaaaa@aaa.com", "safdsd", "sdfsdf", "sdfsdfsdf");
        usersDB.createUser(u1);
        usersDB.createUser(u2);

        // conflict on update
        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(JSON)
                .cookie(c).put(Entity.json(new User(true, Role.ADMIN, "aaaaa@aaa.com", null, "Matteo", "Zeni")));
        assertEquals(409, r1.getStatus());
    }

    @Test
    public void deleteUserSuccess() throws Exception {

        // login as ADMIN
        final Cookie c = loginAdmin();

        // create a user
        final User u = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u);

        // try delete
        final Response response = getTarget().path("/users/" + u.getUid()).request(JSON).cookie(c).delete();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteUserFail1() throws Exception {

        // login as CLIENT
        final Cookie c = loginClient();

        // create a user
        final User u = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u);

        // try delete
        final Response response = getTarget().path("/users/" + u.getUid()).request(JSON).cookie(c).delete();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void deleteUserFail2() throws Exception {

        // create a user
        final User u = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        usersDB.createUser(u);

        // try delete (no session)
        final Response response = getTarget().path("/users/" + u.getUid()).request(JSON).delete();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void deleteUserFail3() throws Exception {

        // login as CLIENT
        final Cookie c = loginAdmin();

        // try delete (not found)
        final Response response = getTarget().path("/users/" + 35234).request(JSON).cookie(c).delete();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void getTopClientsSuccess() throws Exception {

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

        // login as admin
        final Cookie c = loginAdmin();

        // request
        final Response response = getTarget().path("/users/top").request(JSON).cookie(c).get();
        assertEquals(200, response.getStatus());

        // current
        final List<TopClient> current = response.readEntity(new GenericType<List<TopClient>>() {
        });

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void getTopClientsFail1() throws Exception {

        // client login
        final Cookie c = loginClient();

        // get data
        final Response r1 = getTarget().path("/users/top").request(JSON).cookie(c).get();
        assertEquals(401, r1.getStatus());
    }

    @Test
    public void getTopClientsFail2() throws Exception {

        // no login

        // get data
        final Response r1 = getTarget().path("/users/top").request(JSON).get();
        assertEquals(401, r1.getStatus());
    }

}

