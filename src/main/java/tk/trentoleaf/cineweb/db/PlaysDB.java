package tk.trentoleaf.cineweb.db;

import org.joda.time.DateTime;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.exceptions.db.AnotherFilmScheduledException;
import tk.trentoleaf.cineweb.exceptions.db.ConstrainException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;
import tk.trentoleaf.cineweb.model.Film;
import tk.trentoleaf.cineweb.model.Play;
import tk.trentoleaf.cineweb.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PlaysDB {
    private final Logger logger = Logger.getLogger(PlaysDB.class.getSimpleName());

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
    public void createPlay(Play play) throws SQLException, ConstrainException, AnotherFilmScheduledException {

        // TODO: transaction
        boolean another = isAlreadyPlay(play.getRid(), play.getTime());
        if (another) {
            throw new AnotherFilmScheduledException();
        }

        final String query = "INSERT INTO plays (pid, fid, rid, time, _3d) " +
                "VALUES (DEFAULT, ?, ?, ?, ?) RETURNING pid;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, play.getFid());
            stm.setInt(2, play.getRid());
            stm.setTimestamp(3, new Timestamp(play.getTime().toDate().getTime()));
            stm.setBoolean(4, play.is_3d());
            ResultSet rs = stm.executeQuery();

            // if here -> no error
            rs.next();
            play.setPid(rs.getInt("pid"));
        } catch (PSQLException e) {
            throw new ConstrainException(e);
        }
    }

    // check play
    public boolean isAlreadyPlay(Room room, DateTime time) throws SQLException {
        return isAlreadyPlay(room.getRid(), time);
    }

    // check play
    public boolean isAlreadyPlay(int rid, DateTime time) throws SQLException {
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
        }
    }

    // check if is older play
    public boolean isOlderPlay(int pid, DateTime time) throws SQLException {

        boolean isOlderPlay = false;
        final String query = "SELECT time FROM plays " +
                "WHERE pid = ? ;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {

            stm.setInt(1, pid);

            ResultSet rs = stm.executeQuery();
            rs.next();


            if (rs.getTimestamp(1).before(new Timestamp(time.toDate().getTime()))) {
                isOlderPlay = true;
            }
        }
        return isOlderPlay;
    }

    // get list of plays
    public List<Play> getPlays() throws SQLException {
        final List<Play> plays = new ArrayList<>();

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT pid, fid, rid, time, _3d FROM plays;");

            while (rs.next()) {
                int pid = rs.getInt("pid");
                int fid = rs.getInt("fid");
                int rid = rs.getInt("rid");
                DateTime time = new DateTime(rs.getTimestamp("time").getTime());
                boolean _3d = rs.getBoolean("_3d");
                plays.add(new Play(pid, fid, rid, time, _3d));
            }
        }

        return plays;
    }

    // get a play by id
    public Play getPlay(int pid) throws SQLException, EntryNotFoundException {
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

            throw new EntryNotFoundException();
        }
    }

    // delete a play
    public void deletePlay(Play play) throws SQLException, EntryNotFoundException {
        deletePlay(play.getPid());
    }

    // delete a play
    public void deletePlay(int pid) throws SQLException, EntryNotFoundException {
        final String query = "DELETE FROM plays WHERE pid = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, pid);

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        }
    }


}
