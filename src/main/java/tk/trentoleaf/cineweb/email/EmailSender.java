package tk.trentoleaf.cineweb.email;


import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.util.logging.Logger;

public class EmailSender {
    private static final Logger logger = Logger.getLogger(EmailSender.class.getSimpleName());

    // constants
    public static final String WE = "Cineweb";
    public static final String FROM = "cineweb@trentoleaf.tk";

    private SendGrid sendgrid;

    // singleton
    private static EmailSender sender;

    public static EmailSender instance() {
        if (sender == null) {
            sender = new EmailSender();
        }
        return sender;
    }

    private EmailSender() {
        final String username = System.getenv("SENDGRID_USERNAME");
        final String password = System.getenv("SENDGRID_PASSWORD");

        if (username == null || password == null) {
            throw new RuntimeException("Please set the system variables SENDGRID_USERNAME & SENDGRID_PASSWORD");
        }

        this.sendgrid = new SendGrid(username, password);
    }

    // send an email to confirm the registration
    public void sendRegistrationEmail(String emailAddress) throws SendGridException {

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(emailAddress);
        email.setSubject(WE + " - Conferma registrazione");
        email.setText("TODO -> text by stefano, HTML to set");

        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }
}
