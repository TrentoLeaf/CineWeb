package tk.trentoleaf.cineweb;

import org.joda.time.DateTime;
import org.junit.Test;
import tk.trentoleaf.cineweb.email.EmailSender;
import tk.trentoleaf.cineweb.beans.model.Role;
import tk.trentoleaf.cineweb.beans.model.User;
import tk.trentoleaf.cineweb.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.pdf.PdfCreator;

import java.io.FileOutputStream;

public class PdfTest {

    private FilmTicketData testData() {
        return new FilmTicketData(12, "pippo@gmail.com", "Titolo film", 2, 3, 4, DateTime.now(), "intero", 34);
    }

    @Test
    public void generatePDF() throws Exception {

        PdfCreator pdfCreator = new PdfCreator();

        boolean res = pdfCreator.addTicketToPdf(testData());
        System.out.println(res);

        res = pdfCreator.addTicketToPdf(testData());
        System.out.println(res);

        res = pdfCreator.addTicketToPdf(testData());
        System.out.println(res);

        // get pdf
        byte[] pdf = pdfCreator.getFilledPdf();

        // convert array of bytes into file
        FileOutputStream fileOuputStream = new FileOutputStream("/tmp/cineweb.pdf");
        fileOuputStream.write(pdf);
        fileOuputStream.close();

        System.out.println("End. PDF available at \"/tmp/cineweb.pdf\".");
    }

}
