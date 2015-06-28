package tk.trentoleaf.cineweb.heroku;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.webapp.*;

/**
 * This class launches the web application in an embedded Jetty container. This is the entry point to your application.
 * The Java command that is used for launching should fire this main method.
 */
public class Main {

    /**
     * WebApp entry point.
     *
     * @param args Command line parameters. NB: they are ignored.
     * @throws Exception If it is not possible to start the embedded Jetty server.
     */
    public static void main(String[] args) throws Exception {

        // get the server
        final Server server = createServer();

        // start the server
        server.start();
        server.join();
    }

    /**
     * Create an embedded Jetty server.
     *
     * @return The embedded Jetty server. NB: it must be started.
     */
    public static Server createServer() {

        // The port that we should run on can be set into an environment variable
        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        // create a server that handles X-Forwarded-Proto
        final Server server = new Server();
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.addCustomizer(new ForwardedRequestCustomizer());
        ServerConnector serverConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        serverConnector.setPort(Integer.valueOf(webPort));
        server.addConnector(serverConnector);

        final WebAppContext root = new WebAppContext();
        root.setContextPath("/");

        final String webappDirLocation = "src/main/webapp/";
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        // Configuration classes. This gives support for multiple features.
        // The annotationConfiguration is required to support annotations like @WebServlet
        root.setConfigurations(new Configuration[]{
                new AnnotationConfiguration(), new WebXmlConfiguration(),
                new WebInfConfiguration(),
                new PlusConfiguration(), new MetaInfConfiguration(),
                new FragmentConfiguration(), new EnvConfiguration()});

        // Important! make sure Jetty scans all classes under ./classes looking for annotations. Classes
        // directory is generated running 'mvn package'
        root.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/classes/.*");

        // Parent loader priority is a class loader setting that Jetty accepts.
        // By default Jetty will behave like most web containers in that it will
        // allow your application to replace non-server libraries that are part of the
        // container. Setting parent loader priority to true changes this behavior.
        // Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);

        server.setHandler(root);

        // return the server
        return server;
    }

}
