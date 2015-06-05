package tk.trentoleaf.cineweb.email.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.nio.file.Files;

public class FullFillPDF{

    public static File fillPDF (FilmTicketData data) {

        File pdfTemplate = null;
        File pdfOutput = null;

        try {

            // get the template
            pdfTemplate = new File("src/main/webapp/emails/pdf-email/ticket.pdf");
            // create a new file for output
            pdfOutput = new File("src/main/webapp/emails/pdf-email/ticket-filled.pdf");

            // open the streams
            InputStream inputStream = new FileInputStream(pdfTemplate);
            OutputStream outputStream = new FileOutputStream(pdfOutput);


            PdfReader pdfReader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);

            // fill the PDF
            AcroFields fields = stamper.getAcroFields();
            fields.setField("pdf-title", data.getTitle());
            fields.setField("pdf-theatre", data.getTheatre());
            fields.setField("pdf-seat", data.getSeat());
            fields.setField("pdf-date", data.getDate());
            fields.setField("pdf-time", data.getTime());
            fields.setField("pdf-type", data.getType());

            // TRYYYYYYY
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, outputStream);
            doc.open();

            Font font = new Font(Font.FontFamily.HELVETICA, 12);
            BaseFont baseFont = font.getBaseFont();

            PdfContentByte cb = writer.getDirectContent();

            cb.saveState();
            cb.beginText();
            cb.moveText(150, 200);
            cb.setFontAndSize(baseFont, 12);
            cb.showText("PROVAaaa");
            cb.endText();
            cb.restoreState();

doc.close();




            // print the QR-code
            BarcodeQRCode qrcode = new BarcodeQRCode(data.getQrCode(), 175, 175, null);

            PdfContentByte content = stamper.getOverContent(pdfReader.getNumberOfPages());

            Image image = qrcode.getImage();
            image.setAbsolutePosition(335f, 550f);
            content.addImage(image);

            // make fields not editable
           stamper.setFormFlattening(true);

            stamper.close();
            pdfReader.close();

            return pdfOutput;

        } catch (BadElementException e) {
            e.printStackTrace();
            try {
                Files.delete(pdfOutput.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        } catch (DocumentException e) {
            e.printStackTrace();
            try {
                Files.delete(pdfOutput.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                Files.delete(pdfOutput.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Files.delete(pdfOutput.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            try {
                Files.delete(pdfOutput.toPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }
}