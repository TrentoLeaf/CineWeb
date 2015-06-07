package tk.trentoleaf.cineweb.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class RestRootTest extends MyJerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(RestRoot.class);
    }

    @Test
    public void testMsgWorking() {
        final Response response = target().path("/").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Jersey Working!", response.readEntity(String.class));
    }
}

