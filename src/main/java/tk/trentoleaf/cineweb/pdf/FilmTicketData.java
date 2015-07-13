package tk.trentoleaf.cineweb.pdf;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import tk.trentoleaf.cineweb.utils.Utils;

/**
 * Represent the content a the ready-to-print ticket contained in the PDF attachment.
 */
public class FilmTicketData {

    private int ticketID;
    private String email;
    private String title;
    private String room;
    private String seat;
    private String date;
    private String time;
    private String type;
    private double price;

    public FilmTicketData(int tid, String email, String title, int rid, int x, int y, DateTime time, String type, double price) {
        this.ticketID = tid;
        this.email = email;
        this.title = title;
        this.room = "Sala " + rid;
        this.seat = Utils.getCharForNumber(x) + y;
        this.time = DateTimeFormat.forPattern("HH:mm").print(time);
        this.date = DateTimeFormat.forPattern("MM/dd/yyyy").print(time);
        this.type = type;
        this.price = price;
    }

    public String getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    public String getRoom() {
        return room;
    }

    public String getSeat() {
        return seat;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Return the String from which to generate the QrCode.
     *
     * @return String for the QrCode.
     */
    public String generateQrCodeDataString() {
        return "{\n" +
                "   number:  " + ticketID + ",\n" +
                "   email:   " + email + ",\n" +
                "   title:   " + title + ",\n" +
                "   room:    " + room + ",\n" +
                "   seat:    " + seat + ",\n" +
                "   date:    " + date + ",\n" +
                "   time:    " + time + ",\n" +
                "   type:    " + type + ",\n" +
                "   price:   " + price + "\n" +
                '}';
    }

}


