package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.beans.model.FilmGrossing;
import tk.trentoleaf.cineweb.beans.model.TopClient;
import tk.trentoleaf.cineweb.exceptions.db.DBException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        if (instance == null) {
            instance = new StatisticsDB();
        }
        return instance;
    }

    // force to use the singleton
    private StatisticsDB() {
    }

    // list of films grossing
    public List<FilmGrossing> getFilmsGrossing() throws DBException {
        final List<FilmGrossing> gg = new ArrayList<>();

        final String query = "WITH tmp AS (SELECT fid, SUM(price) AS grossing FROM films NATURAL JOIN plays " +
                "NATURAL JOIN tickets WHERE deleted = FALSE GROUP BY fid) " +
                "SELECT fid, grossing FROM films NATURAL LEFT JOIN tmp;";

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                FilmGrossing g = new FilmGrossing();
                g.setFid(rs.getInt("fid"));
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

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT uid, COUNT(tid) AS tickets, SUM(price) AS spent FROM " +
                    "bookings NATURAL JOIN tickets WHERE deleted = FALSE GROUP BY uid LIMIT 10");

            while (rs.next()) {
                TopClient c = new TopClient();
                c.setUid(rs.getInt("uid"));
                c.setTickets(rs.getInt("tickets"));
                c.setSpent(rs.getDouble("spent"));
                topClients.add(c);
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return topClients;
    }

}
