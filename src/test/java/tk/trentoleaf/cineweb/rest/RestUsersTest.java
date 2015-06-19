package tk.trentoleaf.cineweb.rest;

import org.junit.Test;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class RestUsersTest extends MyJerseyTest {

    @Test
    public void getUserSuccess() throws Exception {

        // login as admin
        final Cookie c = loginAdmin();

        // create a user
        final User u = new User(true, Role.CLIENT, "email@email.com", "pass", "name", "name");
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

}

