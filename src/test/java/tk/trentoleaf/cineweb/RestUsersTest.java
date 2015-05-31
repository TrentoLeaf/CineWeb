package tk.trentoleaf.cineweb;

import org.apache.commons.collections4.CollectionUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.rest.RestUsers;
import tk.trentoleaf.cineweb.rest.entities.Auth;
import tk.trentoleaf.cineweb.rest.entities.ChangePassword;
import tk.trentoleaf.cineweb.rest.entities.ForgotPassword;
import tk.trentoleaf.cineweb.rest.entities.Registration;
import tk.trentoleaf.cineweb.rest.handlers.BadRequestHandler;
import tk.trentoleaf.cineweb.rest.handlers.ConflictHandler;
import tk.trentoleaf.cineweb.rest.handlers.NotFoundHandler;
import tk.trentoleaf.cineweb.rest.utils.GsonJerseyProvider;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RestUsersTest extends MyJerseyTest {

    private static final String COOKIE_NAME = "JSESSIONID";

    @Override
    protected Application configure() {
        return new ResourceConfig(RestUsers.class)
                .register(GsonJerseyProvider.class)
                .register(BadRequestHandler.class)
                .register(ConflictHandler.class)
                .register(NotFoundHandler.class);
    }

    @Test
    public void testLoginSuccess1() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        final Response response = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(new Auth("teo@teo.com", "teo")));
        assertEquals(200, response.getStatus());
        assertTrue(response.getCookies().containsKey(COOKIE_NAME));
    }

    @Test
    public void testLoginSuccess2() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        final Response r1 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(new Auth("teo@teo.com", "teo")));
        assertEquals(200, r1.getStatus());
        assertTrue(r1.getCookies().containsKey(COOKIE_NAME));
        final String c1 = r1.getCookies().get(COOKIE_NAME).toString();

        final Response r2 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).cookie(COOKIE_NAME, c1).post(Entity.json(new Auth("teo@teo.com", "teo")));
        assertEquals(200, r2.getStatus());
        assertTrue(r2.getCookies().containsKey(COOKIE_NAME));
        final String c2 = r2.getCookies().get(COOKIE_NAME).toString();

        // check if 2 sessions
        assertNotEquals(c1, c2);
    }

    @Test
    public void testLoginFail1() {
        final Response response = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(null);
        assertEquals(400, response.getStatus());
        assertFalse(response.getCookies().containsKey(COOKIE_NAME));
    }

    @Test
    public void testLoginFail2() {
        final Response response = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(new Auth("stefano@ste.com", "ccc")));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testLogout() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // login
        final Response responseLogin = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(new Auth("teo@teo.com", "teo")));
        assertTrue(responseLogin.getCookies().containsKey(COOKIE_NAME));
        final String oldCookie = responseLogin.getCookies().get(COOKIE_NAME).getValue();

        // logout
        final Response responseLogout = getTarget().path("/users/logout").request(MediaType.APPLICATION_JSON_TYPE).post(null);
        assertNotEquals(oldCookie, responseLogout.getCookies().get(COOKIE_NAME));
    }

    @Test
    public void testChangePasswordSuccess() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // change password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePassword("teo@teo.com", "teo", "new")));
        assertEquals(200, response.getStatus());

        // try login with old password
        final Response responseLogin1 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Auth("teo@teo.com", "teo")));
        assertEquals(404, responseLogin1.getStatus());

        // try login with new password
        final Response responseLogin2 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Auth("teo@teo.com", "new")));
        assertEquals(200, responseLogin2.getStatus());
    }

    @Test
    public void testChangePasswordFail1() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // change password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePassword("asdadasdas", "teo", "new")));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testChangePasswordFail2() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // change password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePassword("teo@teo.com", "sdfsdfds", "new")));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testChangePasswordFail3() throws Exception {

        // change password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(null));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testChangePasswordFail4() throws Exception {

        // create a user
        db.createUser(new User(true, Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // change password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePassword("teo@teooooo.com", "sdfsdfds", "new")));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void registrationSuccess() throws Exception {

        // create a users (REST)
        final Response response = getTarget().path("/users/registration").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Registration("email@trentoleaf.tk", "password", "name", "surname")));
        assertEquals(200, response.getStatus());

        final User current = db.getUser("email@trentoleaf.tk");
        final User expected = new User(false, Role.CLIENT, "email@trentoleaf.tk", "password", "name", "surname");
        expected.setUid(current.getUid());

        // test
        assertEquals(expected, current);
    }

    @Test
    public void registrationFail1() throws Exception {

        // create a users (REST)
        final Response response = getTarget().path("/users/registration").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Registration(null, "password", "name", "surname")));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void registrationFail2() throws Exception {

        // create a users (REST)
        final Response response = getTarget().path("/users/registration").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(null));
        assertEquals(400, response.getStatus());
    }

    @Test
    public void registrationFail3() throws Exception {

        // create a users (REST)
        final Response r1 = getTarget().path("/users/registration").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Registration("email@trentoleaf.tk", "password", "name", "surname")));
        assertEquals(200, r1.getStatus());

        // create a users (REST) -> already exists
        final Response r2 = getTarget().path("/users/registration").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Registration("email@trentoleaf.tk", "password", "name", "surname")));
        assertEquals(409, r2.getStatus());
    }

    @Test
    public void forgotPasswordSuccess() throws Exception {

        // create a user
        db.createUser(new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // forgot password request
        final Response r1 = getTarget().path("/users/forgot-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ForgotPassword("teo@teo.com")));
        assertEquals(200, r1.getStatus());
    }

    @Test
    public void forgotPasswordFail1() throws Exception {

        // create a user
        db.createUser(new User(false, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // forgot password request
        final Response r1 = getTarget().path("/users/forgot-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ForgotPassword("teo@teo.com")));
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void forgotPasswordFail2() throws Exception {

        // create a user
        db.createUser(new User(false, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // forgot password request
        final Response r1 = getTarget().path("/users/forgot-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ForgotPassword(null)));
        assertEquals(400, r1.getStatus());
    }

    @Test
    public void forgotPasswordFail3() throws Exception {

        // create a user
        db.createUser(new User(false, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // forgot password request
        final Response r1 = getTarget().path("/users/forgot-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ForgotPassword("aaaaaaa@aaa.com")));
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void getUsers() throws Exception {
        final Response response = getTarget().path("/users").request(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
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

