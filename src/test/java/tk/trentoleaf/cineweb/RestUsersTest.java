package tk.trentoleaf.cineweb;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO: admin areas
public class RestUsersTest extends MyJerseyTest {

    @Test
    public void getUsers() throws Exception {
        final Response response = getTarget().path("/users").request(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getUserSuccess() throws Exception {

        // create a user
        final User u = new User(true, Role.CLIENT, "email@email.com", "pass", "name", "name");
        db.createUser(u);

        // search the user
        final Response r1 = getTarget().path("/users/" + u.getUid()).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, r1.getStatus());
        assertEquals(u, r1.readEntity(User.class));
    }

    @Test
    public void getUserFail() throws Exception {
        final Response r1 = getTarget().path("/users/" + 34).request(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void createUserSuccess() throws Exception {

        // create a user
        final Response response = getTarget().path("/users/").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni")));
        assertEquals(200, response.getStatus());

        // test
        final List<User> expected = new ArrayList<>();
        expected.add(response.readEntity(User.class));

        // current
        final List<User> current = db.getUsers();

        // test
        assertTrue(CollectionUtils.isEqualCollection(expected, current));
    }

    @Test
    public void createUserFail1() throws Exception {

        // create a user
        getTarget().path("/users/").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni")));

        // create a user -_> should fail
        final Response response = getTarget().path("/users/").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni")));
        assertEquals(409, response.getStatus());
    }

    @Test
    public void createUserFail2() throws Exception {

        // create a user -_> should fail
        final Response response = getTarget().path("/users/").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new User(true, Role.ADMIN, "sdf2", null, "Matteo", "Zeni")));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void updateUserSuccess() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(new User(true, Role.ADMIN, "a@a.com", null, "Matteo", "Zeni")));
        assertEquals(200, r1.getStatus());

        final User current = db.getUser("a@a.com");
        final User expected = new User(true, Role.ADMIN, "a@a.com", null, "Matteo", "Zeni");
        expected.setUid(current.getUid());

        // test
        assertEquals(expected, current);
    }

    @Test
    public void updateUserFail1() throws Exception {

        final Response response = getTarget().path("/users/").request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(new User(true, Role.ADMIN, "sdf2", null, "Matteo", "Zeni")));
        assertEquals(405, response.getStatus());
    }

    @Test
    public void updateUserFail2() throws Exception {

        final Response r1 = getTarget().path("/users/" + 2345).request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(new User(true, Role.ADMIN, "sdf2", null, "Matteo", "Zeni")));
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void updateUserFail3() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u1);

        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(new User(true, Role.ADMIN, "sdf2", null, null, "Zeni")));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void updateUserFail4() throws Exception {

        final User u1 = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        final User u2 = new User(true, Role.ADMIN, "aaaaa@aaa.com", "safdsd", "sdfsdf", "sdfsdfsdf");
        db.createUser(u1);
        db.createUser(u2);

        final Response r1 = getTarget().path("/users/" + u1.getUid()).request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(new User(true, Role.ADMIN, "aaaaa@aaa.com", null, "Matteo", "Zeni")));
        assertEquals(409, r1.getStatus());
    }

    @Test
    public void deleteUserSuccess() throws Exception {

        // create a user
        final User u = new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u);

        // try delete
        final Response response = getTarget().path("/users/" + u.getUid()).request(MediaType.APPLICATION_JSON_TYPE).delete();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteUserFail() throws Exception {

        // try delete
        final Response response = getTarget().path("/users/" + 35234).request(MediaType.APPLICATION_JSON_TYPE).delete();
        assertEquals(404, response.getStatus());
    }

}

