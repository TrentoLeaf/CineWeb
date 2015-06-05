package tk.trentoleaf.cineweb;

/**
 * Created by andrea on 6/5/15.
 */

import org.junit.Test;
import tk.trentoleaf.cineweb.email.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.email.pdf.PdfCreator;

import java.io.File;


public class PdfTest {

    @Test
    public void test() {


        PdfCreator pdf = new PdfCreator();
        boolean res = pdf.addTicketToPdf(new FilmTicketData());
        System.out.println(res);
        res = pdf.addTicketToPdf(new FilmTicketData());
        res = pdf.addTicketToPdf(new FilmTicketData());
        File pdfAttachment = pdf.getFilledPdf();
        System.out.println(pdfAttachment);
        System.out.println("FinisheD...");

    }



}
