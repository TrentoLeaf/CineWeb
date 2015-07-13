package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import tk.trentoleaf.cineweb.beans.model.Play;
import tk.trentoleaf.cineweb.beans.model.Room;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.exceptions.db.DBException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the plays storage. It uses the connections provided by the DB class.
 *
 * @see tk.trentoleaf.cineweb.db.DB
 */
public class PlaysDB {

    // get a DB instance
    protected DB db = DB.instance();

    // instance -> singleton pattern
    private static PlaysDB instance;

    // instance -> singleton pattern
    public static PlaysDB instance() {
        if (instance == null) {
            instance = new PlaysDB();
        }
        return instance;
    }

    // force to use the singleton
    private PlaysDB() {
    }

    // create play
    public void createPlay(Play play) throws DBException, AnotherFilmScheduledException {

        boolean another = isAlreadyPlay(play.getRid(), play.getTime());
        if (another) {
            throw new AnotherFilmScheduledException();
        }

        final String query = "INSERT INTO plays (pid, fid, rid, time, _3d) VALUES (DEFAULT, ?, ?, ?, ?) RETURNING pid;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, play.getFid());
            stm.setInt(2, play.getRid());
            stm.setTimestamp(3, new Timestamp(play.getTime().toDate().getTime()));
            stm.setBoolean(4, play.is_3d());
            ResultSet rs = stm.executeQuery();

            // if here -> no error
            rs.next();
            play.setPid(rs.getInt("pid"));

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // check play
    public boolean isAlreadyPlay(Room room, DateTime time) throws DBException {
        return isAlreadyPlay(room.getRid(), time);
    }

    // check play
    public boolean isAlreadyPlay(int rid, DateTime time) throws DBException {
        final String query = "SELECT COUNT(*) FROM films f NATURAL JOIN plays p " +
                "WHERE p.rid = ? AND p.time <= ? AND ? <= p.time + (f.duration * INTERVAL '1 minute');";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            final Timestamp timestamp = new Timestamp(time.toDate().getTime());

            stm.setInt(1, rid);
            stm.setTimestamp(2, timestamp);
            stm.setTimestamp(3, timestamp);

            ResultSet rs = stm.executeQuery();
            rs.next();

            return rs.getInt(1) >= 1;

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get list of plays (also gone plays)
    public List<Play> getPlays() throws DBException {
        final List<Play> plays = new ArrayList<>();

        final String query = "WITH all_seats AS (SELECT pid, rid, x, y FROM plays NATURAL JOIN seats EXCEPT " +
                "SELECT pid, rid, x ,y FROM plays NATURAL JOIN tickets), " +
                "not_free AS (SELECT pid, rid, count(*) AS free FROM all_seats GROUP BY pid, rid) " +
                "SELECT pid, rid, fid, time, _3d, free FROM plays NATURAL JOIN not_free;";

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                int pid = rs.getInt("pid");
                int fid = rs.getInt("fid");
                int rid = rs.getInt("rid");
                DateTime time = new DateTime(rs.getTimestamp("time").getTime());
                boolean _3d = rs.getBoolean("_3d");
                int free = rs.getInt("free");
                plays.add(new Play(pid, fid, rid, time, _3d, free));
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return plays;
    }

    // get a list of not gone plays
    public List<Play> getFuturePlays() throws DBException {
        final List<Play> plays = new ArrayList<>();

        final String query = "WITH all_seats AS (SELECT pid, rid, x, y FROM plays NATURAL JOIN seats EXCEPT " +
                "SELECT pid, rid, x ,y FROM plays NATURAL JOIN tickets), " +
                "not_free AS (SELECT pid, rid, count(*) AS free FROM all_seats GROUP BY pid, rid) " +
                "SELECT pid, rid, fid, time, _3d, free FROM plays NATURAL JOIN not_free WHERE time >= now();";

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                int pid = rs.getInt("pid");
                int fid = rs.getInt("fid");
                int rid = rs.getInt("rid");
                DateTime time = new DateTime(rs.getTimestamp("time").getTime());
                boolean _3d = rs.getBoolean("_3d");
                int free = rs.getInt("free");
                plays.add(new Play(pid, fid, rid, time, _3d, free));
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return plays;
    }

    // get a play by id
    public Play getPlay(int pid) throws DBException, EntryNotFoundException {
        final String query = "SELECT fid, rid, time, _3d FROM plays WHERE pid = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int fid = rs.getInt("fid");
                int rid = rs.getInt("rid");
                DateTime time = new DateTime(rs.getTimestamp("time").getTime());
                boolean _3d = rs.getBoolean("_3d");

                return new Play(pid, fid, rid, time, _3d);
            }

            // if here, nothing found
            throw new EntryNotFoundException();

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // delete a play
    public void deletePlay(Play play) throws DBException, EntryNotFoundException {
        deletePlay(play.getPid());
    }

    // delete a play
    public void deletePlay(int pid) throws DBException, EntryNotFoundException {
        final String query = "DELETE FROM plays WHERE pid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

}
