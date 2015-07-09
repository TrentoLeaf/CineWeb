package tk.trentoleaf.cineweb.pdf;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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

    private static String getCharForNumber(int i) {
        return i > -1 && i < 26 ? String.valueOf((char) (i + 65)) : null;
    }

    public FilmTicketData(int tid, String email, String title, int rid, int x, int y, DateTime time, String type, double price) {
        this.ticketID = tid;
        this.email = email;
        this.title = title;
        this.room = "Sala " + rid;
        this.seat = getCharForNumber(x) + y;
        this.time = DateTimeFormat.forPattern("HH:mm").print(time);
        this.date = DateTimeFormat.forPattern("MM/dd/yyyy").print(time);
        this.type = type;
        this.price = price;
    }

    public FilmTicketData(int ticketID, String email, String title, String room, String seat, String date, String time, String type, double price) {
        this.ticketID = ticketID;
        this.email = email;
        this.title = title;
        this.room = room;
        this.seat = seat;
        this.date = date;
        this.time = time;
        this.type = type;
        this.price = price;
    }

    public int getTicketID() {
        return ticketID;
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

    public String generateQrCodeDataString() {
        return "Ticket {" +
                "ticketID=" + ticketID +
                ", email='" + email + '\'' +
                ", title='" + title + '\'' +
                ", room='" + room + '\'' +
                ", seat='" + seat + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}


