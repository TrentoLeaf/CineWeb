package tk.trentoleaf.cineweb.email;


import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.rest.utils.Utils;

import java.net.URI;
import java.util.logging.Logger;

public class EmailSender {
    private static final Logger logger = Logger.getLogger(EmailSender.class.getSimpleName());

    // constants
    public static final String WE = "Cineweb";
    public static final String FROM = "cineweb@trentoleaf.tk";

    private SendGrid sendgrid;

    // singleton
    private static EmailSender sender;

    public static EmailSender instance() throws SendGridException {
        if (sender == null) {
            sender = new EmailSender();
        }
        return sender;
    }

    private EmailSender() throws SendGridException {
        final String username = System.getenv("SENDGRID_USERNAME");
        final String password = System.getenv("SENDGRID_PASSWORD");

        if (username == null || password == null) {
            throw new SendGridException(new RuntimeException("Please set the system variables SENDGRID_USERNAME & SENDGRID_PASSWORD"));
        }

        this.sendgrid = new SendGrid(username, password);
    }

    // send an email to confirm the registration
    public void sendRegistrationEmail(URI uri, User user, String verificationCode) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?c=" + verificationCode;

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Conferma registrazione");
        email.setText(EmailUtils.registrationText(user.getFirstName(), user.getSecondName(), url));

        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }

    // send a password recover
    public void sendRecoverPasswordEmail(URI uri, User user, String code) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?r=" + code;

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Recupero password");
        email.setText("TESTO... " + url);

        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }
}
