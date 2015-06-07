package tk.trentoleaf.cineweb.rest;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
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
        final Film f3 = new Film("Titolo 1", "Genere 1",  "http://bbb.com", "../webapp/img/temporary/img1.jpg", "Descrizione 1", 123);
        final Film f4 = new Film("Titolo 2", "Genere 2", "http://asdf.com", "../webapp/img/temporary/img2.jpg", "Descrizione 2", 234);
        final Film f5 = new Film("Titolo 3", "Genere 3", "http://asdfasdf.com", "../webapp/img/temporary/img3.jpg", "Descrizione 3", 233);
        final Film f6 = new Film("Titolo 4", "Genere 4", "http://asdfdsa.it", "../webapp/img/temporary/img4.jpg", "Descrizione 4", 332);
        final Film f7 = new Film("Titolo 5", "Genere 5", "http://asdfdds.org", "../webapp/img/temporary/img5.jpg", "Descrizione 5", 331);
        final Film f8 = new Film("Titolo 6", "Genere 6", "asdfdsafd", "../webapp/img/temporary/img6.jpg", "Descrizione 6", 555);
        final Film f9 = new Film("Titolo 7", "Genere 7", "asdfFDsa", "../webapp/img/temporary/img7.jpg", "Descrizione 7", 322);
        final Film f10 = new Film( "Titolo 8", "Genere 8", "Asdfds", "../webapp/img/temporary/img8.jpg", "Descrizione 8", 221);
        final Film f11 = new Film("Titolo 9", "Genere 9", "asdffdd", "../webapp/img/temporary/img9.jpg", "Descrizione 9", 227);

        db.createFilm(f1);
        db.createFilm(f2);
        db.createFilm(f3);
        db.createFilm(f4);
        db.createFilm(f5);
        db.createFilm(f6);
        db.createFilm(f7);
        db.createFilm(f8);
        db.createFilm(f9);
        db.createFilm(f10);
        db.createFilm(f11);

        // rooms
        final Room r1 = db.createRoom(23, 12);
        final Room r2 = db.createRoom(2, 5);

        // plays
        final Play p1 = new Play(f1, r2, DateTime.now(), true);
        final Play p2 = new Play(f2, r2, DateTime.now().plusMinutes(1000), false);
        final Play p3 = new Play(f1, r1, DateTime.now().plusMinutes(2000), false);
        final Play p4 = new Play(f2, r1, DateTime.now().plusMinutes(3000), false);
        final Play p5 = new Play(f3, r1, DateTime.now().plusMinutes(4000), false);
        final Play p6 = new Play(f4, r1, DateTime.now().plusMinutes(5000), false);
        final Play p7 = new Play(f5, r1, DateTime.now().plusMinutes(6000), false);
        final Play p8 = new Play(f2, r1, DateTime.now().plusMinutes(7000), false);
        final Play p9 = new Play(f2, r1, DateTime.now().plusMinutes(8000), false);
        final Play p10 = new Play(f2, r1, DateTime.now().plusMinutes(9000), false);
        final Play p11 = new Play(f2, r1, DateTime.now().plusMinutes(10000), false);

        // insert
        db.createPlay(p1);
        db.createPlay(p2);
        db.createPlay(p3);
        db.createPlay(p4);
        db.createPlay(p5);
        db.createPlay(p6);
        db.createPlay(p7);
        db.createPlay(p8);
        db.createPlay(p9);
        db.createPlay(p10);
        db.createPlay(p11);

        return Response.ok().build();
    }
}
