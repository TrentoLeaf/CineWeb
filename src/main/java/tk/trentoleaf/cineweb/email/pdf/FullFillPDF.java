package tk.trentoleaf.cineweb.email.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;

import java.io.*;

public class FullFillPDF extends File{

    public FullFillPDF(FilmTicketData data){
        super("src/main/webapp/emails/pdf-email/filledTicket.pdf");

        FilmTicketData variables = new FilmTicketData();

        File pdfTemplate = new File("src/main/webapp/emails/pdf-email/ticket.pdf");

        File pdfOutput = this;

        try (
                InputStream is = new FileInputStream(pdfTemplate);
                OutputStream os = new FileOutputStream(pdfOutput);
        ) {
            PdfReader pdfReader = new PdfReader(is);
            PdfStamper stamper = new PdfStamper(pdfReader, os);

            AcroFields fields = stamper.getAcroFields();

            fields.setField("pdf-title", variables.getTitle());
            fields.setField("pdf-theatre", variables.getTheatre());
            fields.setField("pdf-seat", variables.getSeat());
            fields.setField("pdf-date", variables.getDate());
            fields.setField("pdf-time", variables.getTime());
            fields.setField("pdf-type", variables.getType());

            BarcodeQRCode qrcode = new BarcodeQRCode(variables.getQrCode(), 175, 175, null);

            PdfContentByte content = stamper.getOverContent(pdfReader.getNumberOfPages());

            Image image = qrcode.getImage();
            image.setAbsolutePosition(335f, 550f);
            content.addImage(image);

            stamper.setFormFlattening(true);

            stamper.close();
            pdfReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}