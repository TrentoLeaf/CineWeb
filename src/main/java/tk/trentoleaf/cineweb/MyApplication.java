package tk.trentoleaf.cineweb;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/*")
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages("tk.trentoleaf.cineweb");
    }

}