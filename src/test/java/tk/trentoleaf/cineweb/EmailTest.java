package tk.trentoleaf.cineweb;

import org.junit.Test;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.pdf.PdfCreator;

import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;

public class EmailTest {

    @Test
    public void sendConfirmRegistrationTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, "andr35ez@gmail.com", "teo", "Davide", "Pedranz");

        // send email
        EmailSender sender = EmailSender.instance();
        sender.sendRegistrationEmail(new URI("http://www.cineweb.herokuapp.com"),user,"stringaDiTest");
    }


    @Test
    public void sendPasswordRecoveryTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, "andr35ez@gmail.com", "teo", "Davide", "Pedranz");

        // send email
        EmailSender sender = EmailSender.instance();
        sender.sendRecoverPasswordEmail(new URI("http://www.cineweb.herokuapp.com"), user, "stringaDiTest");
    }


    @Test
    public void sendTicketsTest() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, "andr35ez@gmail.com", "teo", "Davide", "Pedranz");

        // create tickets data
        ArrayList<FilmTicketData> plays = new ArrayList<FilmTicketData>();
        plays.add(new FilmTicketData());
        plays.add(new FilmTicketData());
        plays.add(new FilmTicketData());

        // send email
        EmailSender sender = EmailSender.instance();
        sender.sendTicketPDFEmail(new URI("http://www.cineweb.herokuapp.com"),user, plays);
    }
}
