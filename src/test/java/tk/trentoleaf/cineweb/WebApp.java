package tk.trentoleaf.cineweb;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tk.trentoleaf.cineweb.heroku.Main;

import static org.junit.Assert.assertEquals;

/**
 * This class launches the web application in an embedded Jetty container. This is the entry point to your application. The Java
 * command that is used for launching should fire this main method.
 */
public class WebApp {

    private Server server;

    @Before
    public void startServer() throws Exception {

        // get the server
        server = Main.createServer();

        // start the server
        server.start();
    }

    @After
    public void shutdownServer() throws Exception {

        // stop the server
        server.stop();
    }

    @Test
    public void testCache() throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet("http://localhost:8080/api");
            HttpResponse response = client.execute(request);

            // check cache header
            String cache = response.getHeaders("Cache-Control")[0].getValue();
            assertEquals("no-cache", cache);
        }
    }


}
