package tk.trentoleaf.cineweb.model;

import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

// TODO: transient film and room???
public class Play implements Serializable {

    private int pid;

    @Min(value = 1, message = "Fid must be valid")
    private int fid;

    @Min(value = 1, message = "Rid must be valid")
    private int rid;

    @NotNull(message = "Time cannot be null")
    private DateTime time;

    private boolean _3d;

    private transient Film film;
    private transient Room room;

    public Play(Film film, Room room, DateTime time, boolean _3d) {
        this.fid = film.getFid();
        this.rid = room.getRid();
        this.time = time;
        this._3d = _3d;
        this.film = film;
        this.room = room;
    }

    public Play(int fid, int rid, DateTime time, boolean _3d) {
        this.fid = fid;
        this.rid = rid;
        this.time = time;
        this._3d = _3d;
    }

    public Play(int pid, int fid, int rid, DateTime time, boolean _3d) {
        this.pid = pid;
        this.fid = fid;
        this.rid = rid;
        this.time = time;
        this._3d = _3d;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public boolean is_3d() {
        return _3d;
    }

    public void set_3d(boolean _3d) {
        this._3d = _3d;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public int hashCode() {
        return pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Play play = (Play) o;

        if (pid != play.pid) return false;
        if (fid != play.fid) return false;
        if (rid != play.rid) return false;
        if (_3d != play._3d) return false;
        return time.equals(play.time);

    }

    @Override
    public String toString() {
        return "Play{" +
                "pid=" + pid +
                ", fid=" + fid +
                ", rid=" + rid +
                ", time=" + time +
                ", _3d=" + _3d +
                ", film=" + film +
                ", room=" + room +
                '}';
    }
}
