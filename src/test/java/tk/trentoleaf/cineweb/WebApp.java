package tk.trentoleaf.cineweb;

import org.apache.http.Header;
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
 * This test launches the web application in an embedded Jetty container.
 * Here it is possible to test WebFilters, WebListener and Servlets.
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

            // request
            HttpGet request = new HttpGet("http://localhost:8080/api");
            HttpResponse response = client.execute(request);

            // check cache header
            String cache = response.getHeaders("Cache-Control")[0].getValue();
            assertEquals("no-cache", cache);

            // check pragma header
            String pragma = response.getHeaders("Pragma")[0].getValue();
            assertEquals("no-cache", pragma);
        }
    }

    @Test
    public void testXProtection() throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            // request
            HttpGet request = new HttpGet("http://localhost:8080/");
            HttpResponse response = client.execute(request);

            // check X-Frame-Options
            String frame = response.getHeaders("X-Frame-Options")[0].getValue();
            assertEquals("DENY", frame);

            // check X-Content-Type-Options
            String type = response.getHeaders("X-Content-Type-Options")[0].getValue();
            assertEquals("nosniff", type);

            // check X-XSS-Protection
            String xss = response.getHeaders("X-XSS-Protection")[0].getValue();
            assertEquals("1; mode=block", xss);
        }
    }

    @Test
    public void testGzipOnStaticFiles() throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            // request
            HttpGet request = new HttpGet("http://localhost:8080/");
            HttpResponse response = client.execute(request);

            // check gzip -> use Vary header
            String vary = response.getHeaders("Vary")[0].getValue();
            assertEquals("Accept-Encoding", vary);
        }
    }

    @Test
    public void testGzipOnApi() throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            // request
            HttpGet request = new HttpGet("http://localhost:8080/api/films");
            HttpResponse response = client.execute(request);

            // check gzip -> use Vary header
            String vary = response.getHeaders("Vary")[0].getValue();
            assertEquals("Accept-Encoding", vary);
        }
    }

    @Test
    public void testGzipOffStaticFiles() throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            // request
            HttpGet request = new HttpGet("http://localhost:8080/img/logo.png");
            HttpResponse response = client.execute(request);

            // check gzip -> use Vary header
            Header[] vary = response.getHeaders("Vary");
            assertEquals(0, vary.length);
        }
    }

    @Test
    public void testGzipOffApi() throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            // request
            HttpGet request = new HttpGet("http://localhost:8080/api");
            HttpResponse response = client.execute(request);

            // check gzip -> use Vary header
            Header[] vary = response.getHeaders("Vary");
            assertEquals(0, vary.length);
        }
    }

}
