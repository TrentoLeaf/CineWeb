package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.beans.model.FilmGrossing;
import tk.trentoleaf.cineweb.beans.model.RoomStatus;
import tk.trentoleaf.cineweb.beans.model.SeatCode;
import tk.trentoleaf.cineweb.beans.model.TopClient;
import tk.trentoleaf.cineweb.exceptions.db.DBException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles the statistics. It uses the connections provided by the DB class.
 *
 * @see DB
 */
public class StatisticsDB {

    // get a DB instance
    protected DB db = DB.instance();

    // instance -> singleton pattern
    private static StatisticsDB instance;

    // instance -> singleton pattern
    public static StatisticsDB instance() {
        synchronized (StatisticsDB.class) {
            if (instance == null) {
                instance = new StatisticsDB();
            }
        }
        return instance;
    }

    // force to use the singleton
    private StatisticsDB() {
    }

    // list of films grossing
    public List<FilmGrossing> getFilmsGrossing() throws DBException {
        final List<FilmGrossing> gg = new ArrayList<>();

        final String query = "WITH tmp AS (SELECT fid, title, SUM(price) AS grossing FROM films NATURAL JOIN plays " +
                "NATURAL JOIN tickets GROUP BY fid, title) " +
                "SELECT fid, title, grossing FROM films NATURAL LEFT JOIN tmp;";

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                FilmGrossing g = new FilmGrossing();
                g.setFid(rs.getInt("fid"));
                g.setTitle(rs.getString("title"));
                g.setGrossing(rs.getDouble("grossing"));
                gg.add(g);
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return gg;
    }

    // list of top clients
    public List<TopClient> getTopClients() throws DBException {
        final List<TopClient> topClients = new ArrayList<>();

        final String query = "SELECT uid, first_name, second_name, COUNT(tid) AS tickets, SUM(price) AS spent " +
                "FROM users NATURAL JOIN bookings NATURAL JOIN tickets " +
                "GROUP BY uid, first_name, second_name ORDER BY COUNT(tid) DESC LIMIT 10;";

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                int uid = rs.getInt("uid");
                String firstName = rs.getString("first_name");
                String secondName = rs.getString("second_name");
                int tickets = rs.getInt("tickets");
                double spent = rs.getDouble("spent");
                topClients.add(new TopClient(uid, firstName, secondName, tickets, spent));
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return topClients;
    }

    // top seats by room
    public RoomStatus getTopSeatsByRoom(int rid) throws DBException, EntryNotFoundException {
        try (Connection connection = db.getConnection()) {

            // find the room
            try (PreparedStatement stm1 = connection.prepareStatement("SELECT rows, cols FROM rooms WHERE rid = ?;")) {
                stm1.setInt(1, rid);

                final ResultSet rs1 = stm1.executeQuery();
                if (rs1.next()) {

                    // get room dimensions
                    final int rows = rs1.getInt("rows");
                    final int cols = rs1.getInt("cols");

                    // prepare result matrix
                    final int[][] result = new int[rows][cols];
                    for (int[] col : result) {
                        Arrays.fill(col, SeatCode.MISSING.getValue());
                    }

                    // available places
                    final String avaibalePlaces = "SELECT x, y FROM seats WHERE rid = ?;";

                    // find available places
                    try (PreparedStatement stm4 = connection.prepareStatement(avaibalePlaces)) {
                        stm4.setInt(1, rid);
                        ResultSet rs4 = stm4.executeQuery();

                        while (rs4.next()) {

                            // get seat
                            final int x = rs4.getInt("x");
                            final int y = rs4.getInt("y");

                            // mark as top
                            result[x][y] = SeatCode.AVAILABLE.getValue();
                        }
                    }

                    // query for top seats
                    final String topSeats = "SELECT x, y FROM tickets WHERE rid = ? GROUP BY x, y ORDER BY count(*) DESC LIMIT 10;";

                    // find top seats
                    try (PreparedStatement stm2 = connection.prepareStatement(topSeats)) {
                        stm2.setInt(1, rid);
                        ResultSet rs2 = stm2.executeQuery();

                        while (rs2.next()) {

                            // get seat
                            final int x = rs2.getInt("x");
                            final int y = rs2.getInt("y");

                            // mark as top
                            result[x][y] = SeatCode.BEST.getValue();
                        }
                    }

                    return new RoomStatus(rid, rows, cols, result);
                }

                // not found
                throw new EntryNotFoundException();
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

}
