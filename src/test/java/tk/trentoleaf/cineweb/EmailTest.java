package tk.trentoleaf.cineweb;

import com.sendgrid.SendGrid;
import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;

import java.net.URI;
import java.util.ArrayList;

public class EmailTest {

    // private String email = "andr35ez@gmail.com";
    private String email = "davide.pedranz@gmail.com";

    private FilmTicketData testData() {
        return new FilmTicketData(12, "pippo@gmail.com", "Titolo film", 2, 3, 4, DateTime.now(), "intero", 34);
    }

    @Test
    public void sendConfirmRegistrationTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, email, "teo", "Davide", "Pedranz");

        // send email
        EmailSender sender = EmailSender.instance();
        SendGrid.Response res = sender.sendRegistrationEmail(new URI("https://cineweb.herokuapp.com"), user, "stringaDiTest");

        System.out.println("Registration mail test: " + res.getMessage());
        System.out.println("Registration mail test: " + res.getCode());
        System.out.println("Registration mail test: " + res.getStatus());
    }


    @Test
    public void sendPasswordRecoveryTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, email, "teo", "Davide", "Pedranz");

        // send email
        EmailSender sender = EmailSender.instance();
        SendGrid.Response res = sender.sendRecoverPasswordEmail(new URI("https://cineweb.herokuapp.com"), user, "stringaDiTest");

        System.out.println("Password recovery mail test: " + res.getMessage());
        System.out.println("Password recovery mail test: " + res.getCode());
        System.out.println("Registration mail test: " + res.getStatus());
    }


    @Test
    public void sendTicketsTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, email, "teo", "Davide", "Pedranz");

        // create tickets data
        ArrayList<FilmTicketData> plays = new ArrayList<>();
        plays.add(testData());
        plays.add(testData());
        plays.add(testData());

        // send email
        EmailSender sender = EmailSender.instance();
        SendGrid.Response res = sender.sendTicketPDFEmail(new URI("https://cineweb.herokuapp.com"), user, plays);

        System.out.println("Tickets mail test: " + res.getMessage());
        System.out.println("Tickets mail test: " + res.getCode());
        System.out.println("Tickets mail test: " + res.getStatus());
    }

}
