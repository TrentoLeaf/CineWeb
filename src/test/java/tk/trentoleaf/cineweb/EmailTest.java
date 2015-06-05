package tk.trentoleaf.cineweb;

import org.junit.Test;
import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.email.EmailUtils;
import tk.trentoleaf.cineweb.email.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.model.User;

import java.io.IOException;
import java.net.URI;

public class EmailTest {

    @Test
    public void test() {
        String s = EmailUtils.registrationText("nome", "cognome", "url");
        String c = EmailUtils.registrationText("Williams", "Rizzi", "www.williams.it");
        String t = EmailUtils.registrationText("Trento", "Leaf+", "www.trentoleaf.tk");


        try {
            EmailSender.sendTicketPDFEmail(URI.create("aaa"),new User(),new FilmTicketData());
        } catch (SendGridException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int a=2;
    }


}