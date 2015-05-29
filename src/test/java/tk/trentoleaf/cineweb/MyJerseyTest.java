package tk.trentoleaf.cineweb;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.rest.handlers.BadRequestHandler;
import tk.trentoleaf.cineweb.rest.handlers.ConflictHandler;
import tk.trentoleaf.cineweb.rest.handlers.NotFoundHandler;
import tk.trentoleaf.cineweb.rest.utils.GsonJerseyProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class MyJerseyTest extends JerseyTest {

    protected final DB db = DB.instance();

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

    // setup GSON + Jersey client
    protected final WebTarget getTarget() {
        Client c = ClientBuilder.newClient();
        c.register(GsonJerseyProvider.class);
        c.register(BadRequestHandler.class);
        c.register(ConflictHandler.class);
        c.register(NotFoundHandler.class);
        return c.target(getBaseUri());
    }

}
