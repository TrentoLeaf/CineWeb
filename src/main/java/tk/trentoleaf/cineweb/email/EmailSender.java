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
    public SendGrid.Response sendRegistrationEmail(URI uri, User user, String verificationCode) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?c=" + verificationCode;
        final String urlLogo = Utils.uriToRoot(uri) + "/img/logo_small.png";

        // get class loader
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // get the text
        InputStream textIs = classloader.getResourceAsStream("email/registration.txt");
        StringWriter textWriter = new StringWriter();
        try {
            IOUtils.copy(textIs, textWriter, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // get the html
        InputStream htmlIs = classloader.getResourceAsStream("email/registration.html");
        StringWriter htmlWriter = new StringWriter();
        try {
            IOUtils.copy(htmlIs, htmlWriter, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // modify the text mail
        String textContent = textWriter.toString();
        textContent = textContent.replace("{{nome}}", user.getFirstName());
        textContent = textContent.replace("{{url_address}}", url);

        // modify the html mail
        String htmlContent = htmlWriter.toString();
        htmlContent = htmlContent.replace("{{logo_img}}", urlLogo);
        htmlContent = htmlContent.replace("{{sito}}", Utils.uriToRoot(uri));
        htmlContent = htmlContent.replace("{{nome}}", user.getFirstName());
        htmlContent = htmlContent.replace("{{cognome}}", user.getSecondName());
        htmlContent = htmlContent.replace("{{email}}", user.getEmail());
        htmlContent = htmlContent.replace("{{url_address}}", url);

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Conferma registrazione");
        email.setText(textContent);
        email.setHtml(htmlContent);

        // send the email
        return sendgrid.send(email);
    }

    // send a password recover
    public SendGrid.Response sendRecoverPasswordEmail(URI uri, User user, String code) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?r=" + code;
        final String urlLogo = Utils.uriToRoot(uri) + "/img/logo_small.png";

        // get class loader
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // get the text
        InputStream textIs = classloader.getResourceAsStream("email/password_recovery.txt");
        StringWriter textWriter = new StringWriter();
        try {
            IOUtils.copy(textIs, textWriter, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // get the html
        InputStream htmlIs = classloader.getResourceAsStream("email/password_recovery.html");
        StringWriter htmlWriter = new StringWriter();
        try {
            IOUtils.copy(htmlIs, htmlWriter, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // modify the text mail
        String textContent = textWriter.toString();
        textContent = textContent.replace("{{nome}}", user.getFirstName());
        textContent = textContent.replace("{{url_address}}", url);

        // modify the html mail
        String htmlContent = htmlWriter.toString();
        htmlContent = htmlContent.replace("{{logo_img}}", urlLogo);
        htmlContent = htmlContent.replace("{{sito}}", Utils.uriToRoot(uri));
        htmlContent = htmlContent.replace("{{nome}}", user.getFirstName());
        htmlContent = htmlContent.replace("{{url_address}}", url);

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Recupero password");
        email.setText(textContent);
        email.setHtml(htmlContent);

        // send the email
        return sendgrid.send(email);
    }

    //send a pdf with the ticket
    public SendGrid.Response sendTicketPDFEmail(URI uri, User user, List<FilmTicketData> data) throws SendGridException, DocumentException {

        // url logo
        final String urlLogo = Utils.uriToRoot(uri) + "/img/logo_small.png";

        // get class loader
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        // get the text
        InputStream textIs = classloader.getResourceAsStream("email/tickets.txt");
        StringWriter textWriter = new StringWriter();
        try {
            IOUtils.copy(textIs, textWriter, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // get the html
        InputStream htmlIs = classloader.getResourceAsStream("email/tickets.html");
        StringWriter htmlWriter = new StringWriter();
        try {
            IOUtils.copy(htmlIs, htmlWriter, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // get the html snippet for tickets summary
        InputStream summaryIs = classloader.getResourceAsStream("email/tickets_summary.html");
        StringWriter writerSummary = new StringWriter();
        try {
            IOUtils.copy(summaryIs, writerSummary, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new SendGridException(e);
        }

        // modify the text mail
        String textContext = textWriter.toString();
        textContext = textContext.replace("{{nome}}", user.getFirstName());

        // modify the html mail
        String htmlContent = htmlWriter.toString();
        String htmlSummary = writerSummary.toString();
        htmlContent = htmlContent.replace("{{logo_img}}", urlLogo);
        htmlContent = htmlContent.replace("{{sito}}", Utils.uriToRoot(uri));
        htmlContent = htmlContent.replace("{{nome}}", user.getFirstName());

        // add the plays summary in the mail
        for (FilmTicketData d : data) {
            String tmp = htmlSummary;

            tmp = tmp.replace("{{titolo}}", d.getTitle());
            tmp = tmp.replace("{{data}}", d.getDate());
            tmp = tmp.replace("{{ora}}", d.getTime());
            tmp = tmp.replace("{{sala}}", d.getRoom());
            tmp = tmp.replace("{{posto}}", d.getSeat());
            tmp = tmp.replace("{{tipo}}", d.getType());

            htmlContent = htmlContent.replace("{{riepilogo}}", tmp + "{{riepilogo}}");
        }
        htmlContent = htmlContent.replace("{{riepilogo}}", "");

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Invio biglietti acquistati");
        email.setText("Ecco a lei il Ticket in allegato in formato PDF!");
        email.setText(textContext);
        email.setHtml(htmlContent);

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
            throw new DocumentException(e);
        }

        // send the email
        return sendgrid.send(email);
    }

}
