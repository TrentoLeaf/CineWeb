package tk.trentoleaf.cineweb;

import org.junit.Test;
import tk.trentoleaf.cineweb.entities.*;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class AuthUsersTest extends MyJerseyTest {

    @Test
    public void testLoginSuccess() throws Exception {

        // login
        final Cookie session = loginClient();

        // check session cookie
        assertNotNull(session);
    }

    @Test
    public void testLoginDifferentSessions() throws Exception {

        // credentials
        final String email = "email@email.tk";
        final String password = "password";

        // create a user
        db.createUser(new User(true, Role.CLIENT, email, password, "Matteo", "Zeni"));

        // login 1
        final Cookie c1 = login(email, password);

        // login 2
        final Cookie c2 = login(email, password);

        // check if 2 sessions are different
        assertNotEquals(c1, c2);
    }

    @Test
    public void testLoginFail1() {

        // bad request
        final Response r1 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(null);
        assertEquals(400, r1.getStatus());
        assertFalse(r1.getCookies().containsKey(COOKIE_NAME));
    }

    @Test
    public void testLoginFail2() {

        // user not found
        final Response r1 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(new Auth("stefano@ste.com", "ccc")));
        assertEquals(404, r1.getStatus());
    }

    @Test
    public void testLogout() throws Exception {

        // login
        final Cookie session = loginClient();

        // logout
        final Response r1 = getTarget().path("/users/logout").request(MediaType.APPLICATION_JSON_TYPE).cookie(session).post(null);
        assertNotEquals(session, r1.getCookies().get(COOKIE_NAME));
    }

    @Test
    public void testChangePasswordSuccess() throws Exception {

        // credentials
        final String email = "email@email.tk";
        final String password = "password";
        final String newPassword = "new";

        // login
        final Cookie session = loginClient(email, password);

        // change password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .cookie(session).post(Entity.json(new ChangePassword(email, password, newPassword)));
        assertEquals(200, response.getStatus());

        // try login with old password
        final Response responseLogin1 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Auth(email, password)));
        assertEquals(404, responseLogin1.getStatus());

        // try login with new password
        final Response responseLogin2 = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Auth(email, newPassword)));
        assertEquals(200, responseLogin2.getStatus());
    }

    @Test
    public void testChangePasswordFail0() throws Exception {

        // create a user
        db.createUser(new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // change password -> FAIL (must be logged)
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePassword("teo@teo.com", "teo", "new")));
        assertEquals(401, response.getStatus());
    }

    @Test
    public void testChangePasswordFail1() throws Exception {

        // credentials
        final String email = "email@email.tk";
        final String password = "password";
        final String newPassword = "new";

        // login
        final Cookie session = loginClient(email, password);

        // change password -> FAIL: wrong email
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .cookie(session).post(Entity.json(new ChangePassword("wrong-email", password, newPassword)));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testChangePasswordFail2() throws Exception {

        // credentials
        final String email = "email@email.tk";
        final String password = "password";
        final String newPassword = "new";

        // login
        final Cookie session = loginClient(email, password);

        // change password -> FAIL: wrong password
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .cookie(session).post(Entity.json(new ChangePassword(email, "wrong-password", newPassword)));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testChangePasswordFail3() throws Exception {

        // credentials
        final String email = "email@email.tk";
        final String password = "password";
        final String newPassword = "new";

        // login
        final Cookie session = loginClient(email, password);

        // change password -> FAIL: wrong credentials
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .cookie(session).post(Entity.json(new ChangePassword("wrong-email", "wrong-password", newPassword)));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testChangePasswordFail4() throws Exception {

        // login
        final Cookie session = loginClient();

        // change password -> FAIL: bad request
        final Response response = getTarget().path("/users/change-password").request(MediaType.APPLICATION_JSON_TYPE)
                .cookie(session).post(Entity.json(null));
        assertEquals(400, response.getStatus());
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
    public void confirmRegistrationSuccess() throws Exception {

        // create a users (REST)
        final Response r1 = getTarget().path("/users/registration").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new Registration("email@trentoleaf.tk", "password", "name", "surname")));
        assertEquals(200, r1.getStatus());

        // get registration code
        final String code = db.getConfirmationCode("email@trentoleaf.tk");

        // confirm user
        final Response r2 = getTarget().path("/users/confirm").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ConfirmCode(code)));
        assertEquals(200, r2.getStatus());
        assertEquals(0, r2.readEntity(ActivateUser.class).getCode());

        // confirm user
        final Response r3 = getTarget().path("/users/confirm").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ConfirmCode(code)));
        assertEquals(200, r3.getStatus());
        assertEquals(1, r3.readEntity(ActivateUser.class).getCode());
    }

    @Test
    public void confirmRegistrationFail() throws Exception {

        // confirm user (not existing)
        final Response r1 = getTarget().path("/users/confirm").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ConfirmCode("sdgfdgds")));
        assertEquals(404, r1.getStatus());

        // confirm user (bad request)
        final Response r2 = getTarget().path("/users/confirm").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(null));
        assertEquals(400, r2.getStatus());

        // confirm user (bad request)
        final Response r3 = getTarget().path("/users/confirm").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ConfirmCode(null)));
        assertEquals(400, r3.getStatus());
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
    public void changePasswordRecoverSuccess() throws Exception {

        // create a user
        db.createUser(new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // forgot password request
        final Response r1 = getTarget().path("/users/forgot-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ForgotPassword("teo@teo.com")));
        assertEquals(200, r1.getStatus());

        // test recover password
        final Response r2 = getTarget().path("/users/change-password-code").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePasswordWithCode("teo@teo.com", db.getResetPasswordCode("teo@teo.com"), "aaa")));
        assertEquals(200, r2.getStatus());

        // login
        assertTrue(db.authenticate("teo@teo.com", "aaa"));
    }

    @Test
    public void changePasswordRecoverFail1() throws Exception {

        // test recover password
        final Response r2 = getTarget().path("/users/change-password-code").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePasswordWithCode("teo@teo.com", null, "aaa")));
        assertEquals(400, r2.getStatus());
    }

    @Test
    public void changePasswordRecoverFail2() throws Exception {

        // create a user
        db.createUser(new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni"));

        // forgot password request
        final Response r1 = getTarget().path("/users/forgot-password").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ForgotPassword("teo@teo.com")));
        assertEquals(200, r1.getStatus());

        // test recover password
        final Response r2 = getTarget().path("/users/change-password-code").request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(new ChangePasswordWithCode("aaa@teo.com", db.getResetPasswordCode("teo@teo.com"), "aaa")));
        assertEquals(404, r2.getStatus());
    }

    @Test
    public void testMeSuccess() throws Exception {

        // create a user
        final User user = new User(true, Role.CLIENT, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(user);

        // login
        final Cookie c = login(user.getEmail(), user.getPassword());

        // ask me page (LOGGED IN)
        final Response r2 = getTarget().path("/users/me").request(MediaType.APPLICATION_JSON_TYPE).cookie(c).get();
        assertEquals(200, r2.getStatus());

        // check current user
        assertNotEquals(new UserDetails(user), r2.readEntity(UserDetails.class));
    }

    @Test
    public void testMeFail() throws Exception {

        // ask me page (NOT LOGGED-IN)
        final Response r1 = getTarget().path("/users/me").request(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(401, r1.getStatus());

        // ask me page (WRONG SESSION)
        final Response r2 = getTarget().path("/users/me").request(MediaType.APPLICATION_JSON_TYPE)
                .cookie(COOKIE_NAME, "asfjksdof").get();
        assertEquals(401, r2.getStatus());
    }

}

