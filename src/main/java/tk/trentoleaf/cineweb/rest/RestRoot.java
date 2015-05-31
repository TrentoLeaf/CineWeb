package tk.trentoleaf.cineweb.rest;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;
import tk.trentoleaf.cineweb.model.Room;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/")
public class RestRoot {

    // db singleton
    private DB db = DB.instance();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "Jersey Working!";
    }

    @POST
    @Path("/load-example-data")
    public Response loadExampleData() throws SQLException, AnotherFilmScheduledException {

        // TODO!

        // films
        final Film f1 = new Film("Teo alla ricerca della pizza perduta", "fantasy", "http://aaa.com", "http://aaaa.org", "trama moltooo lunga", 120);
        final Film f2 = new Film("Marcof e PoketMine", "horror", "http://bbb.com", "http://ccc.org", "trama", 30);
        db.createFilm(f1);
        db.createFilm(f2);

        // rooms
        final Room r1 = db.createRoom(23, 12);
        final Room r2 = db.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(121), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(150), false);

        // insert
        db.createPlay(p1);
        db.createPlay(p2);
        db.createPlay(p3);
        db.createPlay(p4);

        return Response.ok().build();
    }
}
