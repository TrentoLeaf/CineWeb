package tk.trentoleaf.cineweb;

import com.sendgrid.SendGrid;
import org.junit.Ignore;
import org.junit.Test;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;

import java.net.URI;
import java.util.ArrayList;

@Ignore
public class EmailTest {

    @Test
    public void sendConfirmRegistrationTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, "andr35ez@gmail.com", "teo", "Davide", "Pedranz");

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
        final User user = new User(true, Role.ADMIN, "andr35ez@gmail.com", "teo", "Davide", "Pedranz");

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
        final User user = new User(true, Role.ADMIN, "andr35ez@gmail.com", "teo", "Davide", "Pedranz");

        // create tickets data
        ArrayList<FilmTicketData> plays = new ArrayList<>();
        plays.add(new FilmTicketData());
        plays.add(new FilmTicketData());
        plays.add(new FilmTicketData());

        // send email
        EmailSender sender = EmailSender.instance();
        SendGrid.Response res = sender.sendTicketPDFEmail(new URI("https://cineweb.herokuapp.com"), user, plays);

        System.out.println("Tickets mail test: " + res.getMessage());
        System.out.println("Tickets mail test: " + res.getCode());
        System.out.println("Tickets mail test: " + res.getStatus());
    }

}
