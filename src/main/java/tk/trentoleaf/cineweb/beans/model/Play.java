package tk.trentoleaf.cineweb.beans.model;

import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Play bean: represent a Play (a certain film is played in a certain room at a certain time).
 */
public class Play implements Serializable {

    private int pid;

    @Min(value = 1, message = "Fid must be valid")
    private int fid;

    @Min(value = 1, message = "Rid must be valid")
    private int rid;

    @NotNull(message = "Time cannot be null")
    private DateTime time;

    @NotNull(message = "_3d cannot be null")
    private Boolean _3d;

    // number of free places
    private Integer free;

    /**
     * Construct an empty play.
     */
    public Play() {
    }

    /**
     * Construct a play.
     *
     * @param film The played Film.
     * @param room The room in which the film is played.
     * @param time When the film is played.
     * @param _3d  True if the film is in 3D.
     */
    public Play(Film film, Room room, DateTime time, boolean _3d) {
        this(film.getFid(), room.getRid(), time, _3d);
    }

    /**
     * Construct a play.
     *
     * @param fid  The played Film ID.
     * @param rid  The room ID in which the film is played.
     * @param time When the film is played.
     * @param _3d  True if the film is in 3D.
     */
    public Play(int fid, int rid, DateTime time, boolean _3d) {
        this.fid = fid;
        this.rid = rid;
        this.time = time;
        this._3d = _3d;
    }

    /**
     * Construct a play.
     *
     * @param pid  The Play ID.
     * @param fid  The played Film ID.
     * @param rid  The room ID in which the film is played.
     * @param time When the film is played.
     * @param _3d  True if the film is in 3D.
     */
    public Play(int pid, int fid, int rid, DateTime time, boolean _3d) {
        this.pid = pid;
        this.fid = fid;
        this.rid = rid;
        this.time = time;
        this._3d = _3d;
    }

    /**
     * Construct a play.
     *
     * @param pid        The Play ID.
     * @param fid        The played Film ID.
     * @param rid        The room ID in which the film is played.
     * @param time       When the film is played.
     * @param _3d        True if the film is in 3D.
     * @param free Number of free places for this play.
     */
    public Play(int pid, int fid, int rid, DateTime time, boolean _3d, Integer free) {
        this.pid = pid;
        this.fid = fid;
        this.rid = rid;
        this.time = time;
        this._3d = _3d;
        this.free = free;
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

    public Boolean is_3d() {
        return _3d;
    }

    public void set_3d(Boolean _3d) {
        this._3d = _3d;
    }

    public Integer getFree() {
        return free;
    }

    public void setFree(Integer free) {
        this.free = free;
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
        if (!time.equals(play.time)) return false;
        if (!_3d.equals(play._3d)) return false;
        return !(free != null ? !free.equals(play.free) : play.free != null);
    }

}
