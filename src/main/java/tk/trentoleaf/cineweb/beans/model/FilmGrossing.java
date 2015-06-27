package tk.trentoleaf.cineweb.beans.model;

public class FilmGrossing {

    private int fid;
    private double grossing;

    public FilmGrossing() {
    }

    public FilmGrossing(int fid, double grossing) {
        this.fid = fid;
        this.grossing = grossing;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public double getGrossing() {
        return grossing;
    }

    public void setGrossing(double grossing) {
        this.grossing = grossing;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = fid;
        temp = Double.doubleToLongBits(grossing);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilmGrossing that = (FilmGrossing) o;

        if (fid != that.fid) return false;
        return Double.compare(that.grossing, grossing) == 0;
    }
}
