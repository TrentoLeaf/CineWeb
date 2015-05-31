package tk.trentoleaf.cineweb.email.pdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 9-06-14.
 */
public class FilmTicketData {
    /**/
    //Nuovi dati
    /**/
    private String title;
    private String theatre;
    private String seat;
    private String date;
    private String time;
    private String type;
    private String qrCode;

    public FilmTicketData(){
        title = "Titolo 1";
        theatre = "Cinema 1";
        seat = "Posto 1";
        date = "Data 1";
        time = "Orario 1";
        type = "Tipo 1";
        qrCode = "qrCode 1";
    }

    /**/
    //new getters and setters
    /**/
    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheatre() {
        return theatre;
    }

    public void setTheatre(String theatre) {
        this.theatre = theatre;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}