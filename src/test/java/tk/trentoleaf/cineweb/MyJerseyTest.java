package tk.trentoleaf.cineweb;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Before;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.utils.GsonJerseyProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

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
                        } catch (ProcessingException e) {
                            throw new TestContainerException(e);
                        } catch (IOException e) {
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
                .target(getBaseUri());
    }

}
