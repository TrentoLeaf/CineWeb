package tk.trentoleaf.cineweb;

import org.junit.Test;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import java.net.URI;

public class EmailTest {

    @Test
    public void test() {

        final String EMAIL = "email@email.com";

        try {
            final User u = new User(true, Role.CLIENT, EMAIL, "pass", "name", "name");
            EmailSender.sendTicketPDFEmail(URI.create("aaa"), u, new FilmTicketData());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}