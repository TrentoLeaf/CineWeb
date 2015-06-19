package tk.trentoleaf.cineweb.rest;

import org.glassfish.grizzly.compression.zip.GZipEncoder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Before;
import tk.trentoleaf.cineweb.MyApplication;
import tk.trentoleaf.cineweb.db.*;
import tk.trentoleaf.cineweb.beans.rest.Auth;
import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.utils.GsonJerseyProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MyJerseyTest extends JerseyTest {

    protected static final String JSON = MediaType.APPLICATION_JSON;
    protected static final String COOKIE_NAME = "JSESSIONID";

    protected final DB db = DB.instance();
    protected final UsersDB usersDB = UsersDB.instance();
    protected final RoomsDB roomsDB = RoomsDB.instance();
    protected final FilmsDB filmsDB = FilmsDB.instance();
    protected final PlaysDB playsDB = PlaysDB.instance();

    @Before
    public void before() throws Exception {
        db.open();
        db.reset();
        db.init();
    }

    @After
    public void after() throws Exception {
        db.close();
    }

    @Override
    protected Application configure() {
        return new ResourceConfig().packages(MyApplication.class.getPackage().toString());
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new TestContainerFactory() {
            @Override
            public TestContainer create(final URI baseUri, DeploymentContext deploymentContext) throws IllegalArgumentException {
                return new TestContainer() {
                    private HttpServer server;

                    @Override
                    public ClientConfig getClientConfig() {
                        return null;
                    }

                    @Override
                    public URI getBaseUri() {
                        return baseUri;
                    }

                    @Override
                    public void start() {
                        try {
                            this.server = GrizzlyWebContainerFactory.create(baseUri,
                                    Collections.singletonMap("jersey.config.server.provider.packages", "tk.trentoleaf.cineweb")
                            );
                        } catch (ProcessingException | IOException e) {
                            throw new TestContainerException(e);
                        }
                    }

                    @Override
                    public void stop() {
                        this.server.shutdownNow();
                    }
                };
            }
        };
    }

    // setup GSON + Jersey client
    protected final WebTarget getTarget() {
        return ClientBuilder.newClient()
                .register(GsonJerseyProvider.class)
                .register(GZipEncoder.class)
                .target(getBaseUri());
    }

    // login as a given user
    protected final Cookie login(String email, String password) {

        // login
        final Response response = getTarget().path("/users/login").request(JSON)
                .post(Entity.json(new Auth(email, password)));
        assertEquals(200, response.getStatus());

        // check session
        final Cookie c = response.getCookies().get(COOKIE_NAME);
        assertNotNull(c);

        // return session
        return c;
    }

    // create user with ROLE.client and login
    protected final Cookie loginClient(String email, String password) throws Exception {

        // create a user
        usersDB.createUser(new User(true, Role.CLIENT, email, password, "Normal", "User"));

        // login
        return login(email, password);
    }

    // create user with ROLE.client and login
    protected final Cookie loginClient() throws Exception {

        // credentials
        final String email = "email@email.com";
        final String password = "password";

        return loginClient(email, password);
    }

    // create user with ROLE.admin and login
    protected final Cookie loginAdmin(String email, String password) throws Exception {

        // create a user
        usersDB.createUser(new User(true, Role.ADMIN, email, password, "Normal", "User"));

        // login
        return login(email, password);
    }

    // create user with ROLE.admin and login
    protected final Cookie loginAdmin() throws Exception {

        // credentials
        final String email = "admin@email.com";
        final String password = "password";

        return loginAdmin(email, password);
    }

}
