package tk.trentoleaf.cineweb;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/*")
public class MyApplication extends ResourceConfig {

    public MyApplication() {

        // configure rest package -> look in all the project
        packages(MyApplication.class.getPackage().getName());

        // configure the Bean Validator
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }

}