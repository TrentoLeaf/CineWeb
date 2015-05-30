package tk.trentoleaf.cineweb;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.rest.RestUsers;
import tk.trentoleaf.cineweb.rest.entities.Auth;
import tk.trentoleaf.cineweb.rest.handlers.BadRequestHandler;
import tk.trentoleaf.cineweb.rest.handlers.ConflictHandler;
import tk.trentoleaf.cineweb.rest.handlers.NotFoundHandler;
import tk.trentoleaf.cineweb.rest.utils.GsonJerseyProvider;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class RestUsersTest extends MyJerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        return new ResourceConfig(RestUsers.class)
                .register(GsonJerseyProvider.class)
                .register(BadRequestHandler.class)
                .register(ConflictHandler.class)
                .register(NotFoundHandler.class);
    }

    @Test
    public void testLoginSuccess() throws Exception {

        // create a user
        db.createUser(new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));

        final Entity<Auth> ee = Entity.json(new Auth("teo@teo.com", "teo"));
        final Response response = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(ee);
        assertEquals(200, response.getStatus());

        // @Context HttpServletRequest request -> null!

    }

    @Test
    public void testLoginFail1() {
        final Response response = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testLoginFail2() {
        final Response response = getTarget().path("/users/login").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(new Auth("stefano@ste.com", "ccc")));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void deleteUser() throws Exception{

        // create a user
        final User u = new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni");
        db.createUser(u);

        // try delete
        final Response response = getTarget().path("/users/" + u.getUid()).request(MediaType.APPLICATION_JSON_TYPE).delete();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getUsers() throws Exception {
        final Response response = getTarget().path("/users").request(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
    }
}

