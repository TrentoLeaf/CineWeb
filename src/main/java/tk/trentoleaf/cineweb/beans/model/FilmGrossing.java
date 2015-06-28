package tk.trentoleaf.cineweb.beans.model;

/**
 * FilmGrossing: represent the grossing of a given Film.
 */
public class FilmGrossing {

    private int fid;
    private String title;
    private double grossing;

    public FilmGrossing() {
    }

    public FilmGrossing(int fid, String title, double grossing) {
        this.fid = fid;
        this.title = title;
        this.grossing = grossing;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getGrossing() {
        return grossing;
    }

    public void setGrossing(double grossing) {
        this.grossing = grossing;
    }

    @Override
    public int hashCode() {
        return fid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilmGrossing that = (FilmGrossing) o;

        if (fid != that.fid) return false;
        if (Double.compare(that.grossing, grossing) != 0) return false;
        return title.equals(that.title);
    }

}
