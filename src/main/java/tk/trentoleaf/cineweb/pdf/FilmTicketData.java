package tk.trentoleaf.cineweb.pdf;


public class FilmTicketData {

    private String username;
    private String filmTitle;
    private String theatre;
    private String seat;
    private String date;
    private String time;
    private String type;
    private String price;

    public FilmTicketData() {
        username = "mario@srv.com";
        filmTitle = "Titolo 1";
        theatre = "Cinema 1";
        seat = "Posto 1";
        date = "Data 1";
        time = "Orario 1";
        type = "Tipo 1";
        price = "5.00";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = String.format("%.2f", price);
    }


    public String generateQrCodeDataString () {
        return username+"\n"+filmTitle+"\n"+theatre+" "+seat+"\n"+date+" "+time+"\n"+type+"\n"+price;
    }


}


