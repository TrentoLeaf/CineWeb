package tk.trentoleaf.cineweb.email;

import com.itextpdf.text.DocumentException;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import org.apache.commons.io.IOUtils;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.pdf.PdfCreator;
import tk.trentoleaf.cineweb.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class is used to send email through SendGrid.
 */
public class EmailSender {
    private static final Logger logger = Logger.getLogger(EmailSender.class.getSimpleName());

    // constants
    public static final String WE = "Cineweb";
    public static final String FROM = "cineweb@trentoleaf.tk";

    // SendGrid client
    private SendGrid sendgrid;

    // singleton
    private static EmailSender sender;

    // get singleton
    public static EmailSender instance() throws SendGridException {
        if (sender == null) {
            sender = new EmailSender();
        }
        return sender;
    }

    // force singleton pattern
    private EmailSender() throws SendGridException {
        final String username = System.getenv("SENDGRID_USERNAME");
        final String password = System.getenv("SENDGRID_PASSWORD");

        if (username == null || password == null) {
            throw new SendGridException(new RuntimeException("Please set the system variables SENDGRID_USERNAME & SENDGRID_PASSWORD"));
        }

        this.sendgrid = new SendGrid(username, password);
    }

    // send an email to confirm the registration
    public void test() throws SendGridException {

        // load images to put in the PDF
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("email/registration.html");

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {

        }
        String theString = writer.toString();
        theString = theString.replace("{{BASE_URL}}", "https://cineweb.herokuapp.com");


        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo("davide.pedranz@gmail.com");
        email.setSubject(WE + " - Conferma registrazione");
        email.setHtml(theString);

        // try to send, log failures
        try {
            SendGrid.Response response = sendgrid.send(email);

            sendgrid.send(email);

        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }

    // send an email to confirm the registration
    public SendGrid.Response sendRegistrationEmail(URI uri, User user, String verificationCode) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?c=" + verificationCode;
        final String urlLogo = Utils.uriToRoot(uri) + "/img/logo_small.png";

        // get the html mail
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("email/registration.html");

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, Charset.forName("UTF-8"));
        } catch (IOException e) {

        }

        // modify the html mail
        String theString = writer.toString();
        theString = theString.replace("{{logo_img}}", urlLogo);
        theString = theString.replace("{{sito}}", Utils.uriToRoot(uri));
        theString = theString.replace("{{nome}}", user.getFirstName());
        theString = theString.replace("{{cognome}}", user.getSecondName());
        theString = theString.replace("{{email}}", user.getEmail());
        theString = theString.replace("{{url_address}}", url);

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Conferma registrazione");
        email.setText("Gentile " + user.getFirstName() + ",\n" +
                "La Sua richiesta di iscrizione è stata inoltrata con successo." + "\n" +
                "Per completare la Sua iscrizione clicchi su questo link: " + url + "\n");
        email.setHtml(theString);

        // try to send, log failures
        SendGrid.Response response;
        try {
            response = sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
        return response;
    }

    // send a password recover
    public SendGrid.Response sendRecoverPasswordEmail(URI uri, User user, String code) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?r=" + code;
        final String urlLogo = Utils.uriToRoot(uri) + "/img/logo_small.png";

        // get the html mail
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("email/password_recovery.html");

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, Charset.forName("UTF-8"));
        } catch (IOException e) {

        }

        // modify the html mail
        String theString = writer.toString();
        theString = theString.replace("{{logo_img}}", urlLogo);
        theString = theString.replace("{{sito}}", Utils.uriToRoot(uri));
        theString = theString.replace("{{nome}}", user.getFirstName());
        theString = theString.replace("{{url_address}}", url);

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Recupero password");
        email.setText("Gentile " + user.getFirstName() + ",\n" +
                "Come da sua richiesta le abbiamo inviato la mail per ripristinare la sua password.\n" +
                "Per completare questa operazione clicchi sul seguente link: " + url + "\n");
        email.setHtml(theString);

        // try to send, log failures
        SendGrid.Response response;
        try {
            response = sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
        return response;
    }

    //send a pdf with the ticket
    public SendGrid.Response sendTicketPDFEmail(URI uri, User user, List<FilmTicketData> data) throws SendGridException, IOException, DocumentException {

        // url logo
        final String urlLogo = Utils.uriToRoot(uri) + "/img/logo_small.png";

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Invio biglietti acquistati");
        email.setText("Ecco a lei il Ticket in allegato in formato PDF!");

        // generate PDF
        try {

            // create and open a new pdf file
            final PdfCreator pdfCreator = new PdfCreator();

            // add the tickets to the pdf
            for (FilmTicketData d : data) {
                // addTicketToPdf add a ticket to the pdf with data in input
                pdfCreator.addTicketToPdf(d);
            }

            // close the creation of the pdf and get the pdf
            byte[] pdf = pdfCreator.getFilledPdf();

            // add pdf as attachment
            email.addAttachment("cineweb-tickets.pdf", new ByteArrayInputStream(pdf));

        } catch (DocumentException | IOException e) {
            logger.severe(e.toString());
            throw e;
        }

        // get the html mail
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("email/tickets.html");

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, Charset.forName("UTF-8"));
        } catch (IOException e) {

        }

        // get the html snippet for tickets summary
        InputStream isSummary = classloader.getResourceAsStream("email/tickets_summary.html");

        StringWriter writerSummary = new StringWriter();
        try {
            IOUtils.copy(isSummary, writerSummary, Charset.forName("UTF-8"));
        } catch (IOException e) {

        }


        // modify the html mail
        String theString = writer.toString();
        String theStringSummary = writerSummary.toString();
        theString = theString.replace("{{logo_img}}", urlLogo);
        theString = theString.replace("{{sito}}", Utils.uriToRoot(uri));
        theString = theString.replace("{{nome}}", user.getFirstName());

        // add the plays summary in the mail
        for (FilmTicketData d : data) {
            String tmpSummaryString = theStringSummary;

            tmpSummaryString = tmpSummaryString.replace("{{titolo}}", d.getTitle());
            tmpSummaryString = tmpSummaryString.replace("{{data}}", d.getDate());
            tmpSummaryString = tmpSummaryString.replace("{{ora}}", d.getTime());
            tmpSummaryString = tmpSummaryString.replace("{{sala}}", d.getRoom());
            tmpSummaryString = tmpSummaryString.replace("{{posto}}", d.getSeat());
            tmpSummaryString = tmpSummaryString.replace("{{tipo}}", d.getType());

            theString = theString.replace("{{riepilogo}}", tmpSummaryString+"{{riepilogo}}");
        }
        theString = theString.replace("{{riepilogo}}", "");


        // add the content to email
        email.setText("Gentile " + user.getFirstName() + ",\n" +
                "Le abbiamo inviato i biglietti che ha acquistato sul sito di CineWeb.\n" +
                "Li può trovare in allegato alla mail.\n" +
                "Cordiali Saluti. Cineweb.");
        email.setHtml(theString);

        // try to send, log failures
        SendGrid.Response response;
        try {
            response = sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
        return response;
    }
}
