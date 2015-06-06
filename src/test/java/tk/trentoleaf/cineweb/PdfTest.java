package tk.trentoleaf.cineweb;

import org.junit.Test;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.pdf.PdfCreator;

import java.io.FileOutputStream;

public class PdfTest {

    @Test
    public void generatePDF() throws Exception {

        PdfCreator pdfCreator = new PdfCreator();

        boolean res = pdfCreator.addTicketToPdf(new FilmTicketData());
        System.out.println(res);

        res = pdfCreator.addTicketToPdf(new FilmTicketData());
        System.out.println(res);

        res = pdfCreator.addTicketToPdf(new FilmTicketData());
        System.out.println(res);

        // get pdf
        byte[] pdf = pdfCreator.getFilledPdf();

        // convert array of bytes into file
        FileOutputStream fileOuputStream = new FileOutputStream("/tmp/cineweb.pdf");
        fileOuputStream.write(pdf);
        fileOuputStream.close();

        System.out.println("Finished...");
    }

    // TODO: enable test
    // @Test
    public void sendPDF() throws Exception {

        // create user
        final User user = new User(true, Role.ADMIN, "davide.pedranz@gmail.com", "teo", "Davide", "Pedranz");

        // send email
        EmailSender sender = EmailSender.instance();
        sender.sendTicketPDFEmail(user, new FilmTicketData());
    }

}
