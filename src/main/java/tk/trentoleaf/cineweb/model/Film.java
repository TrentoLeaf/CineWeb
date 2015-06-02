package tk.trentoleaf.cineweb.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Film implements Serializable {

    private int fid;
    private String title;
    private String genre;
    private String trailer;
    private String playbill;
    private String plot;
    private int duration;

    public Film() {
    }

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

    // TODO
    public boolean isValid() {
        return StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(genre) && StringUtils.isNotEmpty(trailer)
                && StringUtils.isNotEmpty(playbill) && StringUtils.isNotEmpty(plot) && duration > 0;
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

    @Override
    public String toString() {
        return "Film{" +
                "fid=" + fid +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", trailer='" + trailer + '\'' +
                ", playbill='" + playbill + '\'' +
                ", plot='" + plot + '\'' +
                ", duration=" + duration +
                '}';
    }
}
