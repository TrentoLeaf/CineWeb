package tk.trentoleaf.cineweb.email.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import java.io.*;

public class PdfCreator {
    // TODO cambiare il path
    private static String filePath = "/tmp/pdf-filled.pdf";
    PdfWriter writer = null;
    Document document = null;
    boolean offset = false;
    PdfContentByte canvas = null;


    public PdfCreator () {
        document = new Document();
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        } catch (Exception e) {
            document = null;
            e.printStackTrace();
        }

        offset = false;

        document.open();
        canvas = writer.getDirectContent();
    }


    public boolean addTicketToPdf (FilmTicketData data) {

        if (document == null) {
            return false;
        }

        try {

            // add rectangle
            Rectangle rect = new Rectangle(70,tfy(55),(70+455),tfy((55 + 255)));
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            canvas.rectangle(rect);

            // add line
            Rectangle line = new Rectangle(91,tfy(132), (91 + 1), tfy((132 + 145)));
            line.setBorder(Rectangle.BOX);
            line.setBorderWidth(2);
            canvas.rectangle(line);


            // add logo
            // TODO cambiare il path
            Image logo = Image.getInstance("/tmp/logo.png");

            logo.setAbsolutePosition(215, tfy(85+(46/2)));
            logo.scaleAbsolute(46,46);
            canvas.addImage(logo);


            // create a basefont
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, false);

            // add cineweb
            canvas.beginText();//Font titleFont = new Font(titleBaseFont, 23, Font.BOLD | Font.ITALIC);
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
            canvas.showText(data.getTheatre());
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
            BarcodeQRCode qrcode = new BarcodeQRCode(data.getQrCode(), 175, 175, null);
            Image qrImage = qrcode.getImage();
            qrImage.setAbsolutePosition(340, 550);
            canvas.addImage(qrImage);

            return true;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private float tfy(float y) {
        if (document == null) {
            return -1;
        }
        Rectangle pageSize = document.getPageSize();
        return pageSize.getTop() - y;
    }


    public File getFilledPdf () {

        if (document == null) {
            return null;
        }

        document.close();

        return (new File(filePath));
    }
}
