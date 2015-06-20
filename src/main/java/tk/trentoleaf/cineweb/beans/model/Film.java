package tk.trentoleaf.cineweb.beans.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Film bean: represent a film.
 */
public class Film implements Serializable {

    private int fid;

    @NotEmpty(message = "Title cannot be null")
    private String title;

    @NotEmpty(message = "Genre cannot be null")
    private String genre;

    @NotEmpty(message = "Trailer cannot be null")
    @URL(message = "Trailer must be a valid URL")
    private String trailer;

    @NotEmpty(message = "Playbill cannot be null")
    @URL(message = "Playbill must be a valid URL")
    private String playbill;

    @NotEmpty(message = "Plot cannot be null")
    private String plot;

    @Min(value = 0, message = "Duration must be >= 0 minutes")
    private int duration;

    /**
     * Construct an empty Film.
     */
    public Film() {
    }

    /**
     * Construct a Film.
     *
     * @param title    Film title.
     * @param genre    Film genre.
     * @param trailer  Film trailer absolute URL.
     * @param playbill Film playbill absolute URL.
     * @param plot     Film plot.
     * @param duration Film duration (in minutes).
     */
    public Film(String title, String genre, String trailer, String playbill, String plot, int duration) {
        this.title = title;
        this.genre = genre;
        this.trailer = trailer;
        this.playbill = playbill;
        this.plot = plot;
        this.duration = duration;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int id) {
        this.fid = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getPlaybill() {
        return playbill;
    }

    public void setPlaybill(String playbill) {
        this.playbill = playbill;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        return fid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (fid != film.fid) return false;
        if (duration != film.duration) return false;
        if (!title.equals(film.title)) return false;
        if (genre != null ? !genre.equals(film.genre) : film.genre != null) return false;
        if (trailer != null ? !trailer.equals(film.trailer) : film.trailer != null) return false;
        if (playbill != null ? !playbill.equals(film.playbill) : film.playbill != null) return false;
        return !(plot != null ? !plot.equals(film.plot) : film.plot != null);
    }
}
