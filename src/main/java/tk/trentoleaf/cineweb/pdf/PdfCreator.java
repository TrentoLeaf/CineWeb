package tk.trentoleaf.cineweb.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.net.URL;

/**
 * Class responsible to generate a PDF containing the bought tickets.
 */
public class PdfCreator {

    private float offset;
    private PdfContentByte canvas;
    private ByteArrayOutputStream outputStream;
    private Document document;

    // constructor
    public PdfCreator() throws DocumentException {

        // init
        document = new Document();

        // create in-memory PDF
        // open output stream
        outputStream = new ByteArrayOutputStream();

        // open writer
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // set offset of the ticket in the page
        offset = 0;

        // open the document
        document.open();

        // get a canvas where put the pdf elements
        canvas = writer.getDirectContent();
    }

    // add a ticket to the pdf
    public boolean addTicketToPdf(FilmTicketData data) {

        // load images to put in the PDF
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final URL logoURL = classloader.getResource("pdf/logo.png");
        assert logoURL != null;
        final String logoPath = logoURL.getPath();

        // generate the ticket
        try {

            // add rectangle
            Rectangle rect = new Rectangle(70, tfy(55), (70 + 455), tfy((55 + 255)));
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            canvas.rectangle(rect);

            // add line
            Rectangle line = new Rectangle(91, tfy(132), (91 + 1), tfy((132 + 145)));
            line.setBorder(Rectangle.BOX);
            line.setBorderWidth(2);
            canvas.rectangle(line);

            // add logo
            Image logo = Image.getInstance(logoPath);

            logo.setAbsolutePosition(215, tfy(85 + (46 / 2)));
            logo.scaleAbsolute(46, 46);
            canvas.addImage(logo);

            // create a basefont
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);

            // add cineweb
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 23);
            canvas.setTextMatrix(280, tfy(90));
            canvas.showText("CineWeb");
            canvas.endText();

            // add the headers
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 12);
            canvas.setTextMatrix(123, tfy(175));
            canvas.showText("SALA");
            canvas.endText();

            canvas.beginText();
            canvas.setTextMatrix(265, tfy(175));
            canvas.showText("POSTO");
            canvas.endText();

            canvas.beginText();
            canvas.setTextMatrix(123, tfy(242));
            canvas.showText("DATA");
            canvas.endText();

            canvas.beginText();
            canvas.setTextMatrix(265, tfy(242));
            canvas.showText("ORA");
            canvas.endText();

            // add the content

            // title
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 16);
            canvas.setTextMatrix(97, tfy(147));
            canvas.showText(data.getTitle());
            canvas.endText();

            // theatre
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 20);
            canvas.setTextMatrix(123, tfy(197));
            canvas.showText(data.getRoom());
            canvas.endText();

            // seat
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 20);
            canvas.setTextMatrix(265, tfy(197));
            canvas.showText(data.getSeat());
            canvas.endText();

            // date
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 20);
            canvas.setTextMatrix(123, tfy(264));
            canvas.showText(data.getDate());
            canvas.endText();

            // time
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 20);
            canvas.setTextMatrix(265, tfy(264));
            canvas.showText(data.getTime());
            canvas.endText();

            // type
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 12);
            canvas.setTextMatrix(205, tfy(290));
            canvas.showText(data.getType());
            canvas.endText();

            // add qrcode
            BarcodeQRCode qrcode = new BarcodeQRCode(data.generateQrCodeDataString(), 175, 175, null);
            Image qrImage = qrcode.getImage();
            qrImage.setAbsolutePosition(340, tfy(300));
            canvas.addImage(qrImage);

            // set new position for a future ticket
            updateNexTicketPosition();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            updateNexTicketPosition();
            return false;
        }
    }

    // set the offset for a new ticket and, if necessary, create a new page
    private void updateNexTicketPosition() {
        if (offset == 0) {
            offset = 400;
        } else {
            document.newPage();
            offset = 0;
        }
    }

    // flip y coordinate of the page, and add an offset
    private float tfy(float y) {
        if (document == null) {
            return -1;
        }
        Rectangle pageSize = document.getPageSize();
        return (pageSize.getTop() - y) - offset;
    }

    // return the pdf as a byte array
    public byte[] getFilledPdf() {

        // close pdf
        document.close();

        // return pdf
        return outputStream.toByteArray();
    }

}
